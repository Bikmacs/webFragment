package com.bignerdranch.android.webFragment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.application_practica2.databinding.ProductItemBinding
import com.bignerdranch.android.webFragment.ui.models.Product

class ProductAdapter() : RecyclerView.Adapter<ProductAdapter.ViewHolder>(){

    private lateinit var _context: Context
    private lateinit var items: List<Product>

    companion object Factory{

        fun create(context: Context): ProductAdapter{
            val x = ProductAdapter()
            x._context = context
            return x
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ProductItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvCartImage.text = item.name

    }

    override fun getItemCount(): Int {
        return items.size
    }


    class ViewHolder(binding: ProductItemBinding): RecyclerView.ViewHolder(binding.root) {
        val tvCartImage = binding.productName
    }

    fun refreshProducts(items: List<Product>){
        this.items = items
    }
}