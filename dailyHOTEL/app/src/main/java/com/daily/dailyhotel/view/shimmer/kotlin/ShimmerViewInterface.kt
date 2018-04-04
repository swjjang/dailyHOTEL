package com.daily.dailyhotel.view.shimmer.kotlin

interface ShimmerViewInterface {
    var gradientX: Float

    var isShimmering: Boolean

    val isSetUp: Boolean

    var animationSetupCallback: ShimmerViewHelper.AnimationSetupCallback

    var primaryColor: Int

    var reflectionColor: Int

    fun setLinearGradientWidth(width: Float)
}
