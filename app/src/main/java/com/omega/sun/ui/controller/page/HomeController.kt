package com.omega.sun.ui.controller.page

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.omega.resource.R
import com.omega.sun.ui.controller.base.BaseLifecycleController
import com.omega.ui.splitties.TEXT_COLOR_PRIMARY
import com.omega.ui.splitties.appBarLParams
import com.omega.ui.splitties.appBarLayout
import com.omega.ui.splitties.contentScrollingWithAppBarLParams
import com.omega.ui.splitties.defaultLParams
import com.omega.ui.splitties.matchParent
import com.omega.ui.splitties.materialButtonText
import com.omega.ui.splitties.materialCardView
import com.omega.ui.splitties.wrapContent
import com.omega.ui.widget.wave.SimpleWaveView
import splitties.dimensions.dip
import splitties.resources.txt
import splitties.views.centerText
import splitties.views.dsl.appcompat.toolbar
import splitties.views.dsl.core.NewViewRef
import splitties.views.dsl.core.add
import splitties.views.dsl.core.lParams
import splitties.views.dsl.core.margin
import splitties.views.dsl.core.textView
import splitties.views.dsl.core.verticalLayout
import splitties.views.dsl.core.view
import splitties.views.gravityCenter
import splitties.views.gravityCenterHorizontal
import splitties.views.textAppearance
import com.google.android.material.R as MaterialR

class HomeController : BaseLifecycleController()  {

    private lateinit var  tvTestTitle:TextView
    override fun onCreateView(context: Context): View  = HomePageView(context)
}

class HomePageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :CoordinatorLayout(context, attrs,defStyleAttr){

    private val viewTitleMargin = 8
    private val viewSubTitleMargin = viewTitleMargin/2

    init {
        fitsSystemWindows = true
        add(appBarLayout(theme = R.style.AppTheme_AppBarOverlay) {
            add(toolbar {
                title = "自定义View整理"
                gravity = gravityCenter
                setTitleTextColor(TEXT_COLOR_PRIMARY)
                popupTheme = R.style.AppTheme_PopupOverlay
                (context as? AppCompatActivity)?.setSupportActionBar(this)
            }, defaultLParams())
        }, appBarLParams())
        add(verticalLayout {
            addCustomView("雷达波浪",::SimpleWaveView)
        }, contentScrollingWithAppBarLParams {
            margin = dip(16)
        })
    }

    private fun LinearLayout.addCustomView(viewName:String,createView: NewViewRef<View>) = apply {
        add(textView {
            //textAppearance = MaterialR.style.TextAppearance_AppCompat_Headline
            textAppearance = MaterialR.style.TextAppearance_AppCompat_Title
            text = viewName
            centerText()
        },lParams(wrapContent,wrapContent,gravityCenterHorizontal))
        add(materialCardView {
            add(view(createView),lParams(matchParent,matchParent){
                margin = dip(viewSubTitleMargin)
            })
        },lParams(matchParent,dip(199)){
            margin = dip(viewTitleMargin)
        })
    }
}