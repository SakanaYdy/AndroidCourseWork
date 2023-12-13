package xyz.doikki.dkplayer.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import xyz.doikki.dkplayer.R
import xyz.doikki.dkplayer.fragment.main.ExtensionFragment
import xyz.doikki.dkplayer.fragment.main.ListFragment
import xyz.doikki.dkplayer.fragment.main.PipFragment
import xyz.doikki.dkplayer.util.PIPManager
import xyz.doikki.dkplayer.util.Tag
import xyz.doikki.dkplayer.util.Utils
import xyz.doikki.dkplayer.util.cache.ProxyVideoCacheManager
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory
import xyz.doikki.videoplayer.player.AndroidMediaPlayerFactory
import xyz.doikki.videoplayer.player.PlayerFactory
import xyz.doikki.videoplayer.player.VideoView
import xyz.doikki.videoplayer.player.VideoViewManager
import java.io.*

class MainActivity : BaseActivity<VideoView>(), NavigationBarView.OnItemSelectedListener {

    private val mFragments: MutableList<Fragment> = ArrayList()
    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun enableBack(): Boolean {
        return false
    }

    override fun initView() {
        super.initView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 10000)
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.setOnItemSelectedListener(this)
        mFragments.add(ListFragment())
        mFragments.add(ExtensionFragment())
        mFragments.add(PipFragment())
        supportFragmentManager.beginTransaction()
            .add(R.id.layout_content, mFragments[0])
            .commitAllowingStateLoss()
        mCurrentIndex = 0
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val index: Int
        val itemId = menuItem.itemId
        index = when (itemId) {
            R.id.tab_list -> 0
            R.id.tab_extension -> 1
            R.id.tab_pip -> 2
            else -> 0
        }
        if (mCurrentIndex != index) {
            //切换tab，释放正在播放的播放器
            if (mCurrentIndex == 1) {
                videoViewManager.releaseByTag(Tag.LIST)
                videoViewManager.releaseByTag(Tag.SEAMLESS, false) //注意不能移除
            }
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = mFragments[index]
            val curFragment = mFragments[mCurrentIndex]
            if (fragment.isAdded) {
                transaction.hide(curFragment).show(fragment)
            } else {
                transaction.add(R.id.layout_content, fragment).hide(curFragment)
            }
            transaction.commitAllowingStateLoss()
            mCurrentIndex = index
        }
        return true
    }

    override fun onBackPressed() {
        if (videoViewManager.onBackPress(Tag.LIST)) return
        if (videoViewManager.onBackPress(Tag.SEAMLESS)) return
        super.onBackPressed()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
    }

    companion object {
        @JvmField
        var mCurrentIndex = 0
    }

}