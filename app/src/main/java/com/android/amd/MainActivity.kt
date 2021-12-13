 package com.android.amd

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.amd.databinding.ActivityMainBinding
import com.google.firebase.crashlytics.internal.common.CommonUtils
import java.io.File
import java.util.*
import kotlin.collections.HashMap


@SuppressLint("HardwareIds")
class MainActivity : AppCompatActivity(), SensorEventListener {
    lateinit var binding: ActivityMainBinding
    private var mSensorManager: SensorManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // initialize your android device sensor capabilities
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager


        binding.refreshDeviceID.setOnClickListener {
            getDeviceID()
        }

        binding.androidVersionRefresh.setOnClickListener {
            getDeviceVersion()
        }

        binding.refreshAudioInfo.setOnClickListener {
            getAudioInfo()
        }

        binding.refreshBatteryInfo.setOnClickListener {
            batteryInformation(applicationContext)
        }

        binding.refreshCarrierInfo.setOnClickListener {
            getCarrierInformation(applicationContext)
        }



        binding.refreshCpuInfo.setOnClickListener {
            getCpuInfoMap()
        }

        binding.refreshNetworkInfo.setOnClickListener {
            checkNetworkStatus()
        }

        binding.refreshRootInfo.setOnClickListener {
            getRootInfo()
        }

        binding.refreshKernalInfo.setOnClickListener {
            getKernelInfo()
        }
        binding.refreshStorageInfo.setOnClickListener {
            getStorageInfo()
        }

        binding.refreshEmulatorInfo.setOnClickListener {
            getEmulatorInfo()
        }

        binding.refreshmacInfo.setOnClickListener {
            getMacAddress()
        }

        binding.refreshPasteBoardInfo.setOnClickListener {
            getPasteBoardData()
        }

        binding.refreshRamInfo.setOnClickListener {
            getRamData()
        }

        binding.refreshresystemUpInfo.setOnClickListener {
            getSystemUptime()
        }

        binding.refreshLanguageInfo.setOnClickListener {
            getLocalLanguageData()
        }

        binding.refreshzoneInfo.setOnClickListener {
            getLocalTimeZoneData()
        }

        binding.refreshwifiSSIDInfo.setOnClickListener {
            getWifiSSID()
        }

        binding.refreshBrightnessInfo.setOnClickListener {
            getBrightness()
        }

        binding.refreshresolutionInfo.setOnClickListener {
            getResolution()
        }

        binding.refreshNetworkInfo.setOnClickListener {
            if (isInternetAvailable(applicationContext)) {
                binding.networkInfo.text = "Connected"
            } else {
                binding.networkInfo.text = "Not, Connected"
            }
        }

        binding.refreshRamInfo.setOnClickListener {
            getRamData()
        }

        binding.refreshPasteBoardInfo.setOnClickListener {
            getPasteBoardData()
        }


        if (isInternetAvailable(applicationContext)) {
            binding.networkInfo.text = "Connected"
        } else {
            binding.networkInfo.text = "Not, Connected"
        }

        getAudioInfo()
        getDeviceID()
        getDeviceVersion()
        batteryInformation(applicationContext)
        getCarrierInformation(applicationContext)
        getCpuInfoMap()
        getStorageInfo()
        getDeiceName()
        checkNetworkStatus()
        getKernelInfo()
        getEmulatorInfo()
        getRootInfo()
        getRamData()
        getPasteBoardData()
        getBrightness()
        getMacAddress()
        getResolution()
        getSystemUptime()
        getLocalTimeZoneData()
        getLocalLanguageData()
        getWifiSSID()
    }

    override fun onResume() {

        super.onResume()

        // for the system's orientation sensor registered listeners
        mSensorManager!!.registerListener(
            this, mSensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    private fun getDeviceID() {
        binding.deviceID.text = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    private fun getDeviceVersion() {
        binding.androidVersion.text = android.os.Build.VERSION.SDK_INT.toString()
    }

    private fun getAudioInfo() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (audioManager != null) {
            for (deviceInfo in audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)) {
                if (deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADSET
                ) {
                    binding.audioJackInfo.text = "Wired Headphones or HeadSet connected"
                } else {
                    binding.audioJackInfo.text = "No Headphones or HeadSet connected"
                }
            }


            val media_max_volume: Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val media_min_volume: Int = audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC)
            val current_volume: Int = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

            binding.audioMaxVolume.text = "Maximum Supported Volume:  $media_max_volume"
            binding.audioMinVolume.text = "Minimum Supported Volume:  $media_min_volume"
            binding.audioCurrentVolume.text = "Current Volume:  $current_volume"


        }
    }


    private fun batteryInformation(context: Context) {
        val bm = context.getSystemService(BATTERY_SERVICE) as BatteryManager
        val batteryInfo = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        binding.batteryInfo.text = "$batteryInfo%"
    }

    private fun getCarrierInformation(context: Context) {
        val manager = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val carrierName = manager.networkOperatorName
        binding.carrierInfo.text = carrierName
    }

    private fun getCpuInfoMap(): Map<String, String>? {
        val map: MutableMap<String, String> = HashMap()
        try {
            val s = Scanner(File("/proc/cpuinfo"))
            while (s.hasNextLine()) {
                val vals: List<String> = s.nextLine().split(": ")
                if (vals.size > 1) map[vals[0].trim { it <= ' ' }] = vals[1].trim { it <= ' ' }
            }
        } catch (e: Exception) {
            Log.e("getCpuInfoMap", Log.getStackTraceString(e))
        }
        binding.cpuInfo.text = map.toString()
        return map
    }


    private fun getDeiceName() {
//        val deviceName = Build.MANUFACTURER + " " + Build.MODEL

        val deviceName = Settings.Global.getString(
            applicationContext.contentResolver,
            Settings.Global.DEVICE_NAME
        );
        binding.deviceName.text = deviceName

    }

    private fun getStorageInfo() {
        val freeBytesExternal = File(getExternalFilesDir(null).toString()).freeSpace
        val totalSize = File(getExternalFilesDir(null).toString()).totalSpace
        val total = (totalSize / (1024 * 1024)).toInt()

        val availableMb =
            (freeBytesExternal / (1024 * 1024)).toString() + "Mb out of " + total + "MB"
        binding.storageInfo.text = availableMb.toString()
    }

    private fun getEmulatorInfo() {
        val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val networkOperator = tm.networkOperatorName
        if ("Android" == networkOperator) {
            binding.emulatorInfo.text = "Yes Emulator"
        } else {
            binding.emulatorInfo.text = "No, its real device"
        }
    }

    private fun getKernelInfo() {
        val map: MutableMap<String, String> = HashMap()
        try {
            val s = Scanner(File("/proc/version"))
            while (s.hasNextLine()) {
                val vals: List<String> = s.nextLine().split(": ")
                if (vals.size > 1) map[vals[0].trim { it <= ' ' }] = vals[1].trim { it <= ' ' }
            }
        } catch (e: Exception) {
            Log.e("getKernelInfo", Log.getStackTraceString(e))
        }

        if (map.isNullOrEmpty()){
            binding.kernalInfo.text = System.getProperty("os.version");
        }else {
            binding.kernalInfo.text = map.toString()
        }
    }

    private fun getRootInfo() {
        if (CommonUtils.isRooted(applicationContext)) {
            binding.rootInfo.text = "Yes rooted"
        } else {
            binding.rootInfo.text = "Not rooted"
        }
    }

    private fun checkNetworkStatus() {
        val cm = applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork
        if (activeNetwork == null) {
            binding.networkInfo.text = "Not connected"
        } else {
            binding.networkInfo.text = "Connected"
        }
    }

    fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }



    private fun getPasteBoardData() {
        val clipBoardManager = applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
//        clipBoardManager.addPrimaryClipChangedListener {
            val copiedString = clipBoardManager.primaryClip?.getItemAt(0)?.text?.toString()
            binding.pasteBoardInfo.text = copiedString.toString()

//        }
    }


    private fun getRamData() {
        val runtime: Runtime = Runtime.getRuntime();
        val usedMemInMB: Long = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        val maxHeapSizeInMB: Long = runtime.maxMemory() / 1048576L;
        val availHeapSizeInMB: Long = maxHeapSizeInMB - usedMemInMB;
        var totalMemoryInGb = (runtime.totalMemory() / 1024)


        var avail = (runtime.freeMemory() / 1024)
        var total= (runtime.totalMemory() / 1024)

        binding.ramInfo.text = avail.toString() + " "+ "MB"
        binding.ramTotalInfo.text = total.toString()+ " " + "MB"

    }

    private fun getLocalLanguageData() {
        binding.languageInfo.text =  Locale.getDefault().language
    }

    private fun getLocalTimeZoneData() {
        binding.zoneInfo.text =   TimeZone.getDefault().id.toString()
    }


    private fun getBrightness(){
        val oldBrightness = Settings.System.getInt(
            applicationContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS
        )

        binding.BrightnessInfo.text = oldBrightness.toString()

    }

    private fun getResolution(){
        val collectionViewWidth = Resources.getSystem().displayMetrics.widthPixels
        val collectionViewHeight = Resources.getSystem().displayMetrics.heightPixels

        binding.resolutionWidthInfo.text = "Width"+ " " + collectionViewWidth.toString()
        binding.resolutionHeightInfo.text = "Height"+ " " + collectionViewHeight.toString()
    }

    private fun getSystemUptime(){
       binding.systemUpTimeInfo.text = SystemClock.elapsedRealtime().toString() + " " + "MS"
    }


    private fun getMacAddress(){
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val wInfo = wifiManager.connectionInfo
        val macAddress = wInfo.macAddress

        binding.macInfo.text = macAddress
    }
    private fun getWifiSSID(){
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo

        wifiInfo = wifiManager.connectionInfo
        if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
           var ssid = wifiInfo.ssid
            binding.wifiSSIDInfo.text = ssid
        }



//        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
//        val info = wifiManager.connectionInfo
//        val ssid = info.ssid

//        binding.wifiSSIDInfo.text = ssid

    }


    override fun onSensorChanged(event: SensorEvent) {
        val degree = Math.round(event.values[0]).toFloat()
        binding.sensorInfo.text = degree.toString()
    }

   override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // not in use
    }
}