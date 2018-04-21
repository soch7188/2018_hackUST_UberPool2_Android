package ustchangdong.com.uberpool2
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.github.kittinunf.fuel.Fuel
import com.google.firebase.auth.FirebaseAuth
import com.uber.sdk.rides.client.ServerTokenSession
import com.uber.sdk.rides.client.SessionConfiguration
import com.uber.sdk.rides.client.UberRidesApi
import kotlinx.android.synthetic.main.fragment_ride.*
import kotlinx.android.synthetic.main.fragment_ride.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class RideFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null
    private var mActivity: Activity? = null
    private var mFragment: RideFragment? = null
    private var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_ride, container, false)
        mAuth = FirebaseAuth.getInstance()
        mActivity = this.activity
        mFragment = this

        val items = arrayOf("HKUST")
        val spinnerArrayAdapter = ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_dropdown_item, items)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rootView!!.spinner1!!.adapter = spinnerArrayAdapter
        rootView!!.spinner1!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }
        }

        val items2 = arrayOf("Tseung Kwan O", "LOHAS Park", "Central")
        val spinnerArrayAdapter2 = ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_dropdown_item, items2)
        spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rootView!!.spinner2!!.adapter = spinnerArrayAdapter2
        rootView!!.spinner2!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                when (position){
                    0 -> { // TKO
                        requestUberEstimates().execute(22.3364f, 114.2655f, 22.3119f, 114.2569f)
                    }
                    1 -> { // LOHAS Park
                        requestUberEstimates().execute(22.3364f, 114.2655f, 22.2949f, 114.2730f)
                    }
                    2 -> { // Central
                        requestUberEstimates().execute(22.3364f, 114.2655f, 22.2800f, 114.1588f)
                    }
                }

            }
        }

//        val success = requestUberEstimates().execute(22.3364f, 114.2655f, 22.3119f, 114.2569f)
        return rootView
    }

    private inner class requestUberEstimates : AsyncTask<Float, Void, Boolean>() {
        override fun doInBackground(params: Array<Float>): Boolean {
            val start_lat = params[0]
            val start_long = params[1]
            val end_lat = params[2]
            val end_long = params[3]
            val config = SessionConfiguration.Builder()
                    .setClientId("c_i8gOwl7-TMrXNC21mb-6hZmtYuQqMc")
                    .setServerToken("H0aai3Jz-NUHDTsDK0hv0zbtXo429w09lAF-kHxS")
                    .build()
            val session = ServerTokenSession(config)
            val service = UberRidesApi.with(session).build().createService()
            val response = service.getProducts(start_lat, start_long).execute()
            val products = response.body().products
            val productIdX = products[0].productId
            val productId7 = products[2].productId

            val prices = service.getPriceEstimates(start_lat, start_long, end_lat, end_long).execute().body()


            // products[0123], prices[0123][estimate, high_estimate, low_estimate, surge_multiplier, distance, display_name, currency_code]

            val pickupTimeEstX = service.getPickupTimeEstimate(start_lat, start_long, productIdX).execute()
            var pickTimeX: String ?= null
            if (pickupTimeEstX.body().times.size == 0){
                pickTimeX = "No Vehicle Available"
            } else {
                pickTimeX = (pickupTimeEstX.body().times[0].estimate!!/60).toString() + " Minutes"
            }

            val pickupTimeEst7 = service.getPickupTimeEstimate(start_lat, start_long, productId7).execute()

            var pickTime7: String ?= null
            if (pickupTimeEst7.body().times.size == 0){
                pickTime7 = "No Vehicle Available"
            } else {
                pickTime7 = (pickupTimeEst7.body().times[0].estimate!!/60).toString() + " Minutes"
            }
//            val pickupTimeEstVal = pickupTimeEst.body().times[0] // display_name, estimate, product_id

            mActivity!!.runOnUiThread(Runnable {
                rootView!!.est_pickup_time_x.text = pickTimeX
                rootView!!.est_pickup_time_7.text = pickTime7

                rootView!!.est_price_x.text = prices.prices[0].estimate
                rootView!!.est_price_7.text = prices.prices[2].estimate
            })

            return true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { date_picker_view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "dd.MM.yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            view.date_picker!!.text = sdf.format(cal.time)
        }
        date_picker.setOnClickListener {
            DatePickerDialog(this.context, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        val timeSetListener = TimePickerDialog.OnTimeSetListener { time_picker_view, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            view.time_picker!!.text = SimpleDateFormat("HH:mm", Locale.US).format(cal.time)
        }
        time_picker!!.setOnClickListener {
            TimePickerDialog(this.context, timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE), true).show()
        }
    }

    fun createRide(){

//        const time= req.body.time;
//        const user_id = req.body.user_id;
//        const est_fare = req.body.est_fare;
//        const est_time = req.body.est_time;
//        const from = req.body.from;
//        const to = req.body.to;

        val json = JSONObject()
        json.put("time", System.currentTimeMillis()/1000) // UnixTime
        json.put("user_id", mAuth?.currentUser!!.uid)

        json.put("from", spinner1.selectedItem.toString())
        json.put("to", spinner2.selectedItem.toString())

        json.put("est_fare", "")
        json.put("est_time", "")


        //synchronous call
        val (ignoredRequest, ignoredResponse, result) =
                Fuel.post("http://47.52.96.225/api/v1/rides/create_ride")
                        .body(json.toString())
                        .responseString()

        result.fold(success = {
            println(it.toString())
        }, failure = {
            println(String(it.errorData))
        })

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                RideFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
