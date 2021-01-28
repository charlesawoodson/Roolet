package com.charlesawoodson.roolet

import android.app.Application
import dagger.Component

@Component
interface ApplicationComponent {

}

class RooletApplication : Application() {
    // val appComponent = DaggerApplication
}