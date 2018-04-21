package ustchangdong.com.uberpool2
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_ride.*
import kotlinx.android.synthetic.main.fragment_ride.view.*
import java.text.SimpleDateFormat
import java.util.*

class RideFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_ride, container, false)

        val items = arrayOf("HKUST")
        val spinnerArrayAdapter = ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_dropdown_item, items)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rootView.spinner1!!.adapter = spinnerArrayAdapter
        rootView.spinner1!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }
        }

        val items2 = arrayOf("Tseung Kwan O", "LOHAS Park", "Central")
        val spinnerArrayAdapter2 = ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_dropdown_item, items2)
        spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rootView.spinner2!!.adapter = spinnerArrayAdapter2
        rootView.spinner2!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        view.date_picker!!.text = SimpleDateFormat("dd.MM.yyyy", Locale.US).format(System.currentTimeMillis())
//        view.time_picker!!.text = SimpleDateFormat("HH:mm", Locale.US).format(System.currentTimeMillis())

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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                RideFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
