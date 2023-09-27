package capps.learning.fanremote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import capps.learning.fanremote.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply {
            mySwitch.setOnCheckedChangeListener { _, isChecked ->
                fanRemoteView.changeLabelToWords(isChecked)
            }
        }
    }
}