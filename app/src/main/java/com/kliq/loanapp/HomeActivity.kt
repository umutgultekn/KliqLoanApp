package com.kliq.loanapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kliq.loanapp.databinding.ActivityHomeBinding
import kotlin.math.abs

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private var allLoans: List<Loan> = emptyList()
    private var filteredLoans: List<Loan> = emptyList()
    private var selectedSegment: Int = 0

    private val repository = LoanRepository(MockLoanService(this))

    private val adapter = LoanAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.logoutButton.setOnClickListener { finish() } // no session to clear

        binding.segAll.setOnClickListener { selectedSegment = 0; applyFilter() }
        binding.segActive.setOnClickListener { selectedSegment = 1; applyFilter() }
        binding.segOverdue.setOnClickListener { selectedSegment = 2; applyFilter() }
        binding.segDefault.setOnClickListener { selectedSegment = 3; applyFilter() }
        binding.segPaid.setOnClickListener { selectedSegment = 4; applyFilter() }

        loadData()
    }

    private fun loadData() {
        try {
            allLoans = repository.processAndUpdateLoans()
            applyFilter()
        } catch (e: Exception) {
            Toast.makeText(this, e.localizedMessage ?: "Error", Toast.LENGTH_LONG).show()
        }
    }

    private fun applyFilter() {
        filteredLoans = when (selectedSegment) {
            1 -> allLoans.filter { it.status == "active" }
            2 -> allLoans.filter { it.status == "overdue" }
            3 -> allLoans.filter { it.status == "default" }
            4 -> allLoans.filter { it.status == "paid" }
            else -> allLoans
        }
        updateSummary()
        adapter.submit(filteredLoans)
    }

    private fun updateSummary() {
        var total = 0.0
        var rateSum = 0.0
        for (loan in filteredLoans) {
            total += loan.principal_amount
            rateSum += loan.interest_rate
        }
        val avgRate = if (filteredLoans.isEmpty()) 0.0 else rateSum / filteredLoans.size

        binding.totalLabel.text = "$" + String.format("%.0f", total)
        binding.countLabel.text = "${filteredLoans.size} loans in portfolio"
        binding.avgRateLabel.text = "Avg. interest rate: " + String.format("%.2f", avgRate) + "%"
    }

    private fun colorForStatus(status: String): Int {
        return when (status) {
            "active" -> Color.rgb(46, 184, 115)
            "overdue" -> Color.rgb(242, 158, 38)
            "default" -> Color.rgb(230, 56, 53)
            "paid" -> Color.rgb(140, 140, 148)
            else -> Color.DKGRAY
        }
    }

    private fun colorForType(type: String): Int {
        return when (type) {
            "personal" -> Color.rgb(33, 43, 69)
            "mortgage" -> Color.rgb(41, 128, 186)
            "auto" -> Color.rgb(51, 153, 143)
            "business" -> Color.rgb(148, 87, 36)
            else -> Color.GRAY
        }
    }

    inner class LoanAdapter : RecyclerView.Adapter<LoanAdapter.LoanViewHolder>() {

        private var items: List<Loan> = emptyList()

        fun submit(loans: List<Loan>) {
            items = loans
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loan, parent, false)
            return LoanViewHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
            val loan = items[position]

            holder.nameLabel.text = loan.name

            holder.typeBadge.text = " ${loan.type.uppercase()} "
            holder.typeBadge.setBackgroundColor(colorForType(loan.type))

            holder.statusBadge.text = " ${loan.status.uppercase()} "
            holder.statusBadge.setBackgroundColor(colorForStatus(loan.status))

            holder.amountLabel.text = "$" + String.format("%.0f", loan.principal_amount)
            holder.rateLabel.text = String.format("%.1f", loan.interest_rate) + "% interest"

            when {
                loan.due_in > 0 -> {
                    holder.dueLabel.text = "${loan.due_in} days remaining"
                    holder.dueLabel.setTextColor(Color.rgb(46, 184, 115))
                }
                loan.due_in == 0 -> {
                    holder.dueLabel.text = "Due today"
                    holder.dueLabel.setTextColor(Color.rgb(242, 158, 38))
                }
                else -> {
                    holder.dueLabel.text = "${abs(loan.due_in)} days overdue"
                    holder.dueLabel.setTextColor(Color.rgb(230, 56, 53))
                }
            }
        }

        inner class LoanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nameLabel: TextView = view.findViewById(R.id.nameLabel)
            val typeBadge: TextView = view.findViewById(R.id.typeBadge)
            val statusBadge: TextView = view.findViewById(R.id.statusBadge)
            val amountLabel: TextView = view.findViewById(R.id.amountLabel)
            val rateLabel: TextView = view.findViewById(R.id.rateLabel)
            val dueLabel: TextView = view.findViewById(R.id.dueLabel)
        }
    }
}
