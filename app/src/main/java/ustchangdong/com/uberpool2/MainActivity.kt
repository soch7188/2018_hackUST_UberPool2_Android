package ustchangdong.com.uberpool2


import android.app.ProgressDialog
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


private var rideFrag: RideFragment? = null
private var scheduleFrag: ScheduleFragment? = null
private var historyFrag: HistoryFragment? = null

private var mAuth: FirebaseAuth? = null
private var currentUser: FirebaseUser? = null

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val transaction = supportFragmentManager.beginTransaction()

        when (item.itemId) {
            R.id.navigation_ride -> {
                transaction.replace(R.id.root_layout, rideFrag)
                transaction.commit()
                supportFragmentManager.executePendingTransactions()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_schedule -> {
                transaction.replace(R.id.root_layout, scheduleFrag)
                transaction.commit()
                supportFragmentManager.executePendingTransactions()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_history -> {
                transaction.replace(R.id.root_layout, historyFrag)
                transaction.commit()
                supportFragmentManager.executePendingTransactions()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth?.getCurrentUser()
        if (currentUser == null) {
            signInAnonymously(savedInstanceState)
        } else {
            populateFragments(savedInstanceState)
        }

        val navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        populateFragments(savedInstanceState)
    }

    private fun populateFragments(savedInstanceState: Bundle?) {
        rideFrag = RideFragment()
        scheduleFrag = ScheduleFragment()
        historyFrag = HistoryFragment()

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById<View>(R.id.root_layout) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return
            }

            // Add the fragment to the 'fragment_container' FrameLayout
            supportFragmentManager.beginTransaction().add(R.id.root_layout, rideFrag).commit()
        }
    }


    private fun signInAnonymously(savedInstanceState: Bundle?) {
        showProgressDialog()
        mAuth?.signInAnonymously()
                ?.addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success")
                            val user = mAuth?.getCurrentUser()
                            populateFragments(savedInstanceState)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException())
                            Toast.makeText(this@MainActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                        }
                        hideProgressDialog()
                    }
                })
    }

    @VisibleForTesting
    var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setMessage("Loading")
            mProgressDialog!!.isIndeterminate = true
        }

        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }
}
