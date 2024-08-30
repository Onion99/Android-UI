package com.omega.sun.ui.controller.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.blankj.utilcode.util.ToastUtils
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import timber.log.Timber

abstract class BaseUIController(args: Bundle? = null) : BaseController(args), LifecycleOwner,
    ViewModelStoreOwner, HasDefaultViewModelProviderFactory {

    val controllerTag = this::class.simpleName

    @Suppress("LeakingThis")
    val lifecycleRegistry = LifecycleRegistry(this)

    // ---- controller 是否可见 ------
    @Volatile
    var controllerViewIsInVisible = true
    private var isFromActivityPause = false

    init {
        addLifecycleListener(object : LifecycleListener() {

            override fun postContextAvailable(controller: Controller, context: Context) {
                super.postContextAvailable(controller, context)
                if (fragmentActivity?.lifecycle?.currentState == Lifecycle.State.INITIALIZED) {
                    fragmentActivity?.lifecycleScope?.launchWhenCreated {
                        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
                    }
                } else {
                    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
                }
            }

            override fun preAttach(controller: Controller, view: View) {
                super.preAttach(controller, view)
                if (lifecycleRegistry.currentState != Lifecycle.State.STARTED) {
                    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
                }
            }

            override fun postAttach(controller: Controller, view: View) {
                super.postAttach(controller, view)
                Timber.tag("Controller").w("${this::class.simpleName} -> onCreateView")
                if (lifecycleRegistry.currentState != Lifecycle.State.RESUMED) {
                    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                }
            }

            override fun preDetach(controller: Controller, view: View) {
                super.preDetach(controller, view)
                if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                }
            }

            override fun postDetach(controller: Controller, view: View) {
                super.postDetach(controller, view)
                Timber.tag("Controller").w("${this::class.simpleName} -> onDestroyView")
                if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                }
            }

            override fun preDestroy(controller: Controller) {
                super.preDestroy(controller)
                // Only act on Controllers that have had at least the onContextAvailable call made on them.
                if (lifecycleRegistry.currentState != Lifecycle.State.INITIALIZED) {
                    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                }
            }
            override fun onChangeEnd(controller: Controller, changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
                when(changeType){
                    ControllerChangeType.PUSH_ENTER -> Unit

                    ControllerChangeType.PUSH_EXIT -> {
                        somethingChangeVisible(false)
                    }
                    ControllerChangeType.POP_ENTER -> {
                        somethingChangeVisible(true)
                    }
                    else -> Unit
                }
            }
        })
    }

    // ------------------------------------------------------------------------
    // 某些东西正在调整页面可见�?,�?般主要调这个
    // ------------------------------------------------------------------------
    fun somethingChangeVisible(visible: Boolean){
        if(visible == controllerViewIsInVisible  || isDestroyed  || !isAttached) return
        controllerViewIsInVisible = visible
        Timber.tag("Controller").w("${this::class.simpleName} -> visibleChange -> $visible")
        isInvisibleChange(controllerViewIsInVisible)
    }

    // ------------------------------------------------------------------------
    // 某些东西正在调整页面可见性真正发生变�?,去重上面的调�?
    // ------------------------------------------------------------------------
    open fun isInvisibleChange(visible: Boolean){}

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        if(isFromActivityPause){
            isFromActivityPause = false
            somethingChangeVisible(true)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)
        if(controllerViewIsInVisible){
            isFromActivityPause = true
            somethingChangeVisible(false)
        }
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    private val controllerViewModelStore: ViewModelStore by lazy(LazyThreadSafetyMode.NONE) { ViewModelStore() }
    override fun getViewModelStore(): ViewModelStore = controllerViewModelStore

    override fun getDefaultViewModelCreationExtras(): CreationExtras {
        /*val application: Application? = activity?.application
        val extras = MutableCreationExtras()
        if (application != null) {
            extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] = application
        }
        //extras[SAVED_STATE_REGISTRY_OWNER_KEY] = this
        extras[VIEW_MODEL_STORE_OWNER_KEY] = this
        extras[DEFAULT_ARGS_KEY] = args*/
        return CreationExtras.Empty
    }

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory =
        ViewModelProvider.NewInstanceFactory.instance


    override fun onDestroy() {
        super.onDestroy()
        viewModelStore.clear()
    }
}
