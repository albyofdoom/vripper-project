package me.vripper.gui

import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.WindowEvent
import me.vripper.gui.event.ApplicationInitialized
import me.vripper.gui.listener.GuiStartupLister
import me.vripper.gui.view.LoadingView
import me.vripper.listeners.AppLock
import me.vripper.utilities.ApplicationProperties.VRIPPER_DIR
import me.vripper.utilities.DbUtils
import org.jetbrains.exposed.sql.Database
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import tornadofx.App
import tornadofx.DIContainer
import tornadofx.FX
import kotlin.reflect.KClass
import kotlin.system.exitProcess


class VripperGuiApplication : App(
    LoadingView::class, Styles::class
) { //The application class must be a TornadoFX application and it must have the main view

    private var initialized = false
    private val startupListener = GuiStartupLister()

    init {
        APP_INSTANCE = this
    }

    override fun start(stage: Stage) {
        with(stage) {
            width = 1366.0
            height = 768.0
            minWidth = 800.0
            minHeight = 600.0
            icons.addAll(
                listOf(
                    Image("icons/16x16.png"),
                    Image("icons/32x32.png"),
                    Image("icons/48x48.png"),
                    Image("icons/64x64.png"),
                    Image("icons/128x128.png"),
                    Image("icons/256x256.png"),
                    Image("icons/512x512.png"),
                    Image("icons/1024x1024.png")
                )
            )
        }
        stage.addEventFilter(WindowEvent.WINDOW_SHOWN) {
            Database.connect("jdbc:h2:file:$VRIPPER_DIR/vripper;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=30000;")
            DbUtils.update()
            startKoin {
                modules(modules)
            }
            startupListener.run()
            FX.dicontainer =
                object : DIContainer {
                    override fun <T : Any> getInstance(type: KClass<T>): T =
                        GlobalContext.get().get(type)
                }
            fire(ApplicationInitialized)
            initialized = true
        }
        super.start(stage)
    }

    override fun stop() { // On stop, we have to stop spring as well
        super.stop()
        if (initialized) {
            //CLOSE
        }
        exitProcess(0)
    }

    companion object {
        lateinit var APP_INSTANCE: Application
    }
}

fun main(args: Array<String>) {
    AppLock.exclusiveLock()
    Application.launch(VripperGuiApplication::class.java, *args)
}
