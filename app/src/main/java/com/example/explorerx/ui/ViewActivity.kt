package com.example.explorerx.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.explorerx.R
import com.example.explorerx.databinding.ActivityViewBinding
import com.example.explorerx.ui.dialog.buildPositiveOnDialog
import com.example.explorerx.utils.isGranted

private const val TAG = "TreeMapActivityx"
private const val EXTERNAL_STORAGE_REQUEST_CODE = 1

class ViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    @RequiresApi(Build.VERSION_CODES.R)
    private val permissionResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (!Environment.isExternalStorageManager()) {
                buildPositiveOnDialog(this) {
                    requestPermissionSdk30()
                }
            } else {
                // if permission granted, do following
                // todo
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.apply {
            setupWithNavController(navController)
        }
        checkPerm { }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_container)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun checkPerm(launch: () -> Unit) {
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                permissionResultLauncher.launch(intent)
            } else {
                launch()
            }
        } else {
            if (!isGranted(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    EXTERNAL_STORAGE_REQUEST_CODE
                )
            } else {
                launch()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestPermissionSdk30() {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.data = Uri.fromParts("package", packageName, null)
        permissionResultLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == EXTERNAL_STORAGE_REQUEST_CODE) {
                // if permission is granted, do following
                // todo
            }
        }
    }
}