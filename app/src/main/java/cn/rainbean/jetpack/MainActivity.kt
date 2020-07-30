package cn.rainbean.jetpack

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import cn.rainbean.jetpack.lifecycle.LocationListener
import cn.rainbean.jetpack.lifecycle.LocationListener.OnLocationChangeListener
import cn.rainbean.jetpack.module.User
import cn.rainbean.jetpack.ui.login.UserManager
import cn.rainbean.jetpack.utils.AppConfig
import cn.rainbean.jetpack.utils.NavGraphBuilder
import cn.rainbean.jetpack.utils.StatusBar
import cn.rainbean.jetpack.widget.AppBottomBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var navController: NavController

    private lateinit var navView:AppBottomBar

    private lateinit var job:Job

    private var locationListener: LocationListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        StatusBar.fitSystemBar(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navView = findViewById(R.id.nav_view)

        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navController = fragment?.let { NavHostFragment.findNavController(it) }!!

        NavGraphBuilder.build(this,fragment?.childFragmentManager,navController,fragment?.id)

        navView.setOnNavigationItemSelectedListener(this)

        locationListener = LocationListener(this, OnLocationChangeListener {
                latitude, longitude ->
                    latitude.toString()
                    longitude.toString()
            }
        )
        lifecycle.addObserver(locationListener!!)

    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(locationListener!!)
        job.cancel()
    }

    override fun onResume() {
        super.onResume()
        job = GlobalScope.launch {

            val startTime = System.currentTimeMillis()

            val result1 = async {
                result1()
            }

            val result2 = async {
                result2()
            }

            val result = result1.await() + result2.await()

            val endTime = System.currentTimeMillis()

            val duration = endTime - startTime

            Log.e("Kotlin协程==","result======$result")
            Log.e("Kotlin协程==","duration======$duration")

        }
    }

    private suspend fun result1():Int{
        delay(5000)
        return 1
    }

    private suspend fun result2():Int{
        delay(6000)
        return 2
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val destConfig = AppConfig.getDestConfig()

        val iterator = destConfig.entries.iterator()

        while (iterator.hasNext()){
            val entry = iterator.next()
            val value = entry.value
            if (value!=null && !UserManager.get().isLogin && value.needLogin && value.id == item.itemId){
                UserManager.get().login(this).observe(this, Observer<User> {
                    navView.selectedItemId = item.itemId
                })
                return false
            }
        }
        navController.navigate(item.itemId)
        return !TextUtils.isEmpty(item.title)
    }

    override fun onBackPressed() {
        val currentPageId = navController.currentDestination?.id

        //APP页面路导航结构图  首页的destinationId
        val startDestination = navController.graph.startDestination

        //如果当前正在显示的页面不是首页，而我们点击了返回键，则拦截。
        if (currentPageId!=startDestination){
            navView.selectedItemId = startDestination
            return
        }
        //否则 finish，此处不宜调用onBackPressed。因为navigation会操作回退栈,切换到之前显示的页面。
        finish()
    }

    /**
     * bugfix:
     * 当MainActivity因为内存不足或系统原因 被回收时 会执行该方法。
     * <p>
     * 此时会触发 FragmentManangerImpl#saveAllState的方法把所有已添加的fragment基本信息给存储起来(view没有存储)，以便于在恢复重建时能够自动创建fragment
     * <p>
     * 但是在fragment嵌套fragment的情况下，被内嵌的fragment在被恢复时，生命周期被重新调度，出现了错误。没有重新走onCreateView 方法
     * 从而会触发throw new IllegalStateException("Fragment " + fname did not create a view.");的异常
     * <p>
     * 但是在没有内嵌fragment的情况，没有问题、
     * <p>
     * <p>
     * 那我们为了解决这个问题，网络上也有许多方案，但都不尽善尽美。
     * <p>
     * 此时我们复写onSaveInstanceState，不让 FragmentManangerImpl 保存fragment的基本数据，恢复重建时，再重新创建即可
     *
     * @param outState
     */
    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
    }


}