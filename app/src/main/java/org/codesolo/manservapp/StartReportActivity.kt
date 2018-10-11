package org.codesolo.manservapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_start_report.*
import kotlinx.android.synthetic.main.content_start_report.*
import java.util.*
import org.codesolo.manservapp.model.Report
import java.util.Date.*


class StartReportActivity : AppCompatActivity() {

    private var reportCode:String? = null
    private var day:Int = 0
    private var month:Int = 0
    private var year:Int = 0
    private var TAG = StartReportActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_report)
        setSupportActionBar(startreport_toolbar)



//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }

        val calendar = Calendar.getInstance()
        this.day = calendar.get(Calendar.DAY_OF_MONTH)
        this.month = calendar.get(Calendar.MONTH)
        this.year = calendar.get(Calendar.YEAR)

        val formatDate = month.toString() + "-" + day.toString() + "-" + year.toString()
        startrep_etx_dateI.setText(formatDate)
        startrep_etx_dateI.setOnClickListener{

            val dialog = DatePickerDialog(this,DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val formatDate = (monthOfYear+1).toString() + "-" + dayOfMonth.toString() + "-" + year.toString()
                startrep_etx_dateI.setText(formatDate)
                },this.year, this.month, this.day)
            dialog.show()
        }

        startrep_etx_dateF.setText(formatDate)
        startrep_etx_dateF.setOnClickListener{

            val dialog = DatePickerDialog(this,DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val formatDate = (monthOfYear+1).toString() + "-" + dayOfMonth.toString() + "-" + year.toString()
                startrep_etx_dateF.setText(formatDate)
            },this.year, this.month, this.day)
            dialog.show()
        }



        startreport_btn_start.setOnClickListener{

            val dbFS = firemanager.firestore!!.collection("reports")

            val report = Report()
//            report.offName = startrep_spn_place.selectedItem.toString()
            report.offName = startrep_etx_agency_name.text.toString()
            report.offCode = "00"
            report.admin = startrep_etx_name.text.toString()
            report.dateStart = startrep_etx_dateI.text.toString()
            report.dateEnd = startrep_etx_dateF.text.toString()
            report.probServ = startrep_etx_name.text.toString()
            report.serScope = startrep_etx_name.text.toString()

            dbFS.add(report).addOnSuccessListener {

                reportCode = it.id

                val intent = Intent(this, ReportActivity::class.java)
                intent.putExtra("reportCode", reportCode)

                startActivity(intent)

            }


        }

        startreport_btn_load.setOnClickListener{

            val offName = startrep_etx_agency_name.text.toString()
            val date = startrep_etx_dateI.text.toString()
            if (offName.isNotEmpty()){
                val dbFS = firemanager.firestore!!.collection("reports")
                var query = dbFS.whereEqualTo("offName",offName).whereEqualTo("dateStart",date)

                query.get().addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty){
                        Log.i(TAG, "no results")
                    } else {
                        for (doc in querySnapshot) {
                            val r  = doc.toObject(Report::class.java)
                            Log.i(TAG,r.toString())
                            reportCode = doc.id
                            val intent = Intent(this, ReportActivity::class.java)
                            intent.putExtra("reportCode", reportCode)

                            startActivity(intent)
                        }
                    }

                }

            }



        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.start_report, menu)
        return super.onCreateOptionsMenu(menu)


    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item!!.itemId) {


        R.id.start_report_menu_exit -> {
            firemanager.authentication!!.signOut()
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
            true
        }
        else ->{
            super.onOptionsItemSelected(item)
        }

    }

}
