package not.working.code.randomvk

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class Core(val title: TextView, val body: TextView, val progress: ProgressBar, val context: Context): AsyncTask<String, String, Void>() {

    val client = OkHttpClient()
    private lateinit var token: String

    var name = ""
    var post = ""
    var id = ""

    override fun onPreExecute() {
        title.text = "Начинаю поиск поста..."
    }

    override fun doInBackground(vararg p0: String): Void? {

        token = p0[0]

        try{
            var JSONPost = getPost()
            publishProgress(context.getString(R.string.extract_data))
            val text = JSONPost.getJSONObject("response").getJSONArray("items").getJSONObject(0).getString("text")
            val userID = JSONPost.getJSONObject("response").getJSONArray("items").getJSONObject(0).getString("owner_id")
            id = userID
            publishProgress(context.getString(R.string.extract_user_data))
            val JSONUser = getUser(userID)
            publishProgress(context.getString(R.string.extract_data))
            try {
                val userName = JSONUser.getJSONArray("response").getJSONObject(0).getString("first_name") + " " + JSONUser.getJSONArray("response").getJSONObject(0).getString("last_name")
                post = text
                name = userName
            } catch (c: Exception) {
                post = context.resources.getStringArray(R.array.many_request)[0]
                name = context.resources.getStringArray(R.array.many_request)[1]
            }
        } catch (e: JSONException) {
            post = context.resources.getStringArray(R.array.server_error)[0]
            name = context.resources.getStringArray(R.array.server_error)[1]
        }
        return null
    }

    override fun onPostExecute(result: Void?) {

        body.text = post
        title.text = name

        title.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://vk.com/id$id"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(context, intent, null)
        }

        progress.visibility = View.GONE

        super.onPostExecute(result)
    }

    override fun onProgressUpdate(vararg values: String?) {
        title.text = values[0]
    }

    private fun getUser(userID: String): JSONObject {
        val request = Request.Builder()
            .url("https://api.vk.com/method/users.get?access_token=$token&user_ids=$userID&v=5.107")
            .build()
        val response = client.newCall(request).execute().body?.string()
        return JSONObject(response)
    }

    private fun getPost(): JSONObject {
        while (true)
        {
            val JSONResponse = JSONObject(getRandomPost(getRandomID()))
            if(JSONResponse.isNull("error"))
                if(JSONResponse.getJSONObject("response").getInt("count") != 0)
                    if (JSONResponse.getJSONObject("response").getJSONArray("items").getJSONObject(0).getString("text") != "")
                        return JSONResponse
        }
    }

    private fun getRandomPost(userID: String): String? {
        val request = Request.Builder()
            .url("https://api.vk.com/method/wall.get?access_token=$token&owner_id=$userID&offset=0&count=1&v=5.107")
            .build()
        val response = client.newCall(request).execute().body?.string()
        return response
    }

    private fun getRandomID(): String {
        var userID = ""
        userID += (1..4).random()
        userID += (1..4).random()
        userID += (0..4).random()
        userID += (0..9).random()
        userID += (0..9).random()
        userID += (0..9).random()
        userID += (0..9).random()
        userID += (0..9).random()
        userID += (0..9).random()
        return userID
    }

}