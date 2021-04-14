package com.tsng.hidemyapplist

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tsng.hidemyapplist.ui.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var permissionError = false

    private fun isModuleActivated(): Boolean { return false }
    private fun isHookSelf(): Boolean { return getSharedPreferences("Settings", MODE_WORLD_READABLE).getBoolean("HookSelf", false) }
    private fun isServiceWorking(): Boolean {
        return try {
            packageManager.getPackageUid("checkHMAServiceStatus", 0)
            true
        } catch (e : PackageManager.NameNotFoundException) { false }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            getSharedPreferences("Settings", MODE_WORLD_READABLE)
            if (isModuleActivated()) {
                if (isServiceWorking()) {
                    xposed_status.setCardBackgroundColor(getColor(R.color.teal))
                    xposed_status_icon.setImageDrawable(getDrawable(R.drawable.ic_activited))
                    xposed_status_text.text = getString(R.string.xposed_activated)
                    xposed_status_sub_text.text = getString(R.string.xposed_service_on)
                } else {
                    xposed_status.setCardBackgroundColor(getColor(R.color.info))
                    xposed_status_icon.setImageDrawable(getDrawable(R.drawable.ic_activited))
                    xposed_status_text.text = getString(R.string.xposed_activated)
                    xposed_status_sub_text.text = getString(R.string.xposed_service_off)
                }
            } else {
                xposed_status.setCardBackgroundColor(getColor(R.color.gray))
                xposed_status_icon.setImageDrawable(getDrawable(R.drawable.ic_not_activated))
                xposed_status_text.text = getString(R.string.xposed_not_activated)
                xposed_status_sub_text.text = getString(if(isServiceWorking())R.string.xposed_service_on else R.string.xposed_service_off)
            }
        } catch (e : SecurityException) {
            permissionError = true
            xposed_status.setCardBackgroundColor(getColor(R.color.error))
            xposed_status_icon.setImageDrawable(getDrawable(R.drawable.ic_not_activated))
            xposed_status_text.text = getString(R.string.xposed_permition_error)
            xposed_status_sub_text.text = getString(R.string.xposed_permition_error_i)
        }
        makeUpdateAlert()
        menu_detection_test.setOnClickListener(this)
        menu_template_manage.setOnClickListener(this)
        menu_scope_manage.setOnClickListener(this)
        menu_settings.setOnClickListener(this)
        menu_about.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.menu_detection_test -> startActivity(Intent(this, DetectionActivity::class.java))
            R.id.menu_template_manage ->
                if (permissionError) Toast.makeText(this, R.string.xposed_permition_error_i, Toast.LENGTH_SHORT).show()
                else if (isHookSelf()) Toast.makeText(this, R.string.xposed_disable_hook_self_first, Toast.LENGTH_SHORT).show()
                else startActivity(Intent(this, TemplateManageActivity::class.java))
            R.id.menu_scope_manage ->
                if (permissionError) Toast.makeText(this, R.string.xposed_permition_error_i, Toast.LENGTH_SHORT).show()
                else if (isHookSelf()) Toast.makeText(this, R.string.xposed_disable_hook_self_first, Toast.LENGTH_SHORT).show()
                else startActivity(Intent(this, ScopeManageActivity::class.java))
            R.id.menu_settings ->
                if (permissionError) Toast.makeText(this, R.string.xposed_permition_error_i, Toast.LENGTH_SHORT).show()
                else startActivity(Intent(this, SettingsActivity::class.java))
            R.id.menu_about -> startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    private fun makeUpdateAlert() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        if (pref.getInt("LastVersion", 0) < BuildConfig.VERSION_CODE) {
            pref.edit().putInt("LastVersion", BuildConfig.VERSION_CODE).apply()
            MaterialAlertDialogBuilder(this).setTitle(R.string.updates)
                    .setMessage(R.string.updates_log)
                    .setPositiveButton(R.string.accept, null)
                    .show()
        }
    }
}