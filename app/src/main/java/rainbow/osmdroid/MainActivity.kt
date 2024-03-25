package rainbow.osmdroid

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.scale
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow
import rainbow.osmdroid.databinding.ActivityMainBinding
import rainbow.osmdroid.databinding.InfoWindowBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", 0));
        setContentView(binding.root)
        binding.mapView.run {
            JSONArray(assets.open("city.json").readBytes().toString(Charsets.UTF_8)).run {
                for (i in 0 until length()) {
                    val jsonObject = getJSONObject(i);
                    overlayManager.add(Marker(binding.mapView).apply {
                        infoWindow = object : InfoWindow(
                            InfoWindowBinding.inflate(layoutInflater).apply {
                                nameTextView.text = jsonObject.getString("City")
                                temperatureTextView.text = Random.nextInt(30).toString()
                            }.root, binding.mapView
                        ) {
                            override fun onOpen(item: Any?) {
                                closeAllInfoWindowsOn(binding.mapView)
                            }

                            override fun onClose() {}

                        }
                        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                        Canvas(bitmap).run {
                            drawColor(Color.WHITE)
                            drawBitmap(
                                getDrawable(
                                    when (Random.nextInt(3)) {
                                        0 -> R.drawable.sun
                                        1 -> R.drawable.cloudy
                                        else -> R.drawable.raining
                                    }
                                )!!.toBitmap().scale(30, 30), 0f, 0f, Paint()
                            )
                        }
                        icon = bitmap.toDrawable(resources)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        position = GeoPoint(
                            jsonObject.getDouble("Latitude"),
                            jsonObject.getDouble("Longitude")
                        )
                    })
                }
            }
            controller.run {
                setZoom(9.0)
                setCenter(GeoPoint(23.97565, 120.9738819))
            }
            setMultiTouchControls(true)
        }
    }
}
