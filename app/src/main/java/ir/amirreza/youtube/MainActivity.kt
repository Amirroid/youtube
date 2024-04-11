package ir.amirreza.youtube

import android.animation.Animator.AnimatorListener
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutCirc
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.EaseOutElastic
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.OneShotPreDrawListener
import ir.amirreza.youtube.ui.theme.YoutubeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    //    var isSplashShowed by mutableStateOf(true)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        splashScreen.setOnExitAnimationListener {
            val view = ComposeView(it.context)
            it.addView(view)
            view.setContent {
                YoutubeTheme {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        SplashIcon {
                            val anim = AlphaAnimation(1f, 0f)
                            anim.duration = 500
                            it.startAnimation(anim)
                            anim.setAnimationListener(object : AnimationListener {
                                override fun onAnimationStart(animation: Animation?) = Unit
                                override fun onAnimationEnd(animation: Animation?) {
                                    it.remove()
                                }

                                override fun onAnimationRepeat(animation: Animation?) = Unit

                            })
                        }
                    }
                }
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YoutubeTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(text = "Home")
                }
            }
        }
    }
}

@Composable
fun SplashIcon(onEnd: () -> Unit) {
    val drawBoxAnimatable = remember {
        Animatable(0f)
    }
    val color = remember {
        Animatable(Color.Red)
    }
    val iconColor = remember {
        Animatable(Color.White)
    }
    val onBackground = MaterialTheme.colorScheme.onBackground
    val progress = remember {
        Animatable(0f)
    }
    val closeAnimation = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = Unit) {
        delay(1000)
        launch {
            drawBoxAnimatable.animateTo(1f, tween(800, easing = EaseInExpo))
        }
        launch {
            color.animateTo(onBackground, tween(800, easing = LinearEasing))
        }
        launch {
            iconColor.animateTo(onBackground, tween(800, easing = LinearEasing))
        }
        delay(350)
        progress.animateTo(1f, tween(700, easing = EaseInExpo))
        delay(200)
        closeAnimation.animateTo(1f, tween(800, easing = EaseInExpo))
        delay(500)
        onEnd.invoke()
    }
    BoxWithConstraints(
        modifier = Modifier
            .offset((-36).dp * drawBoxAnimatable.value)
            .size(200.dp, height = 60.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val triangleSize = Size(36.dp.toPx(), 36.dp.toPx())
            val rectSize = Size(
                size.width * (1 - closeAnimation.value) * (drawBoxAnimatable.value).coerceIn(
                    0.6f,
                    1f
                ),
                (1 - drawBoxAnimatable.value).coerceIn(0.1f, 1f) * size.height
            )
            var xRoundRect =
                drawBoxAnimatable.value * triangleSize.width
                    .plus(15f)
            if (1 - drawBoxAnimatable.value > 0.6f) {
                xRoundRect =
                    (size.width.div(2) - xRoundRect - rectSize.width.div(2)) * (1 - drawBoxAnimatable.value)
            }
            val deltaX = size.width.plus(triangleSize.width).div(2) - xRoundRect
            drawRoundRect(
                color.value,
                size = rectSize,
                topLeft = Offset(
                    xRoundRect + deltaX * closeAnimation.value+ triangleSize.width * closeAnimation.value,
                    size.height.minus(rectSize.height).div(2)
                ),
                cornerRadius = CornerRadius(
                    16.dp.toPx() * (1 - drawBoxAnimatable.value),
                )
            )
            drawRect(
                Color.Red,
                size = rectSize.copy(width = rectSize.width * progress.value),
                topLeft = Offset(
                    xRoundRect + deltaX * closeAnimation.value+ triangleSize.width * closeAnimation.value,
                    size.height.minus(rectSize.height).div(2)
                ),
            )
        }
        Icon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .offset(
                    maxWidth
                        .minus(36.dp)
                        .div(2) * (1 - drawBoxAnimatable.value)
                            + maxWidth
                        .minus(36.dp)
                        .div(2) * closeAnimation.value
                            + 36.dp * closeAnimation.value
                ),
            tint = iconColor.value
        )
    }
}





