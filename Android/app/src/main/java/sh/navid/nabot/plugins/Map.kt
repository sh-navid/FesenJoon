package sh.navid.nabot.plugins

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri


interface IMap{
    fun open(context: Context, lat: Double, lng: Double)
    fun navigate(context: Context, originLat: Double, originLng: Double, destLat: Double, destLng: Double)
}


class NeshanMap : IMap {
    override fun open(context: Context, lat: Double, lng: Double) {
        val uri = Uri.parse("neshan://place?lat=$lat&lng=$lng")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val marketIntent = Intent(Intent.ACTION_VIEW).apply {
                setData(Uri.parse("market://details?id=com.neshan.app"))
            }
            context.startActivity(marketIntent)
        }
    }

    override fun navigate(
        context: Context,
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double
    ) {
        val uri = Uri.parse("neshan://route?origin=$originLat,$originLng&destination=$destLat,$destLng")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val marketIntent = Intent(Intent.ACTION_VIEW).apply {
                setData(Uri.parse("market://details?id=com.neshan.app"))
            }
            context.startActivity(marketIntent)
        }
    }
}


class GoogleMap : IMap {
    override fun open(context: Context, lat: Double, lng: Double) {
        val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val marketIntent = Intent(Intent.ACTION_VIEW).apply {
                setData(Uri.parse("market://details?id=com.google.android.apps.maps"))
            }
            context.startActivity(marketIntent)
        }
    }

    override fun navigate(
        context: Context,
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double
    ) {
        val uri = Uri.parse("google.navigation:q=$destLat,$destLng&mode=d")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val marketIntent = Intent(Intent.ACTION_VIEW).apply {
                setData(Uri.parse("market://details?id=com.google.android.apps.maps"))
            }
            context.startActivity(marketIntent)
        }
    }
}


// Strategy Pattern
class MapContext(private var mapStrategy: IMap):IMap {
    fun setMapStrategy(strategy: IMap) {
        mapStrategy = strategy
    }

    override  fun open(context: Context, lat: Double, lng: Double) {
        mapStrategy.open(context, lat, lng)
    }

    override  fun navigate(
        context: Context,
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double
    ) {
        mapStrategy.navigate(context, originLat, originLng, destLat, destLng)
    }
}
