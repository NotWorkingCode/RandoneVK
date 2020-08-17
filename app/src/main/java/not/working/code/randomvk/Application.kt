package not.working.code.randomvk

import android.app.Application
import android.content.Context
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        VK.addTokenExpiredHandler(tokenTracker)
    }

    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() {
            getSharedPreferences("values", Context.MODE_PRIVATE).edit().remove("token").apply()
        }
    }
}