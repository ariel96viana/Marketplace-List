package com.alvarengadev.marketplacelist.ui.fragments.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.alvarengadev.marketplacelist.R
import com.alvarengadev.marketplacelist.data.models.Item
import com.alvarengadev.marketplacelist.databinding.ItemCartBinding
import com.alvarengadev.marketplacelist.ui.fragments.cart.dialog.delete.DeleteItemDialog
import com.alvarengadev.marketplacelist.ui.fragments.cart.dialog.delete.DeleteItemInterface
import com.alvarengadev.marketplacelist.utils.TextFormatter

class CartAdapter(
    private val listItems: ArrayList<Item>,
    private val supportFragmentManager: FragmentManager
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var onClickItemListener: OnClickItemListener? = null
    private var observerListEmpty: ObserverListEmpty? = null

    fun setOnClickItemListener(onClickItemListener: OnClickItemListener) {
        this.onClickItemListener = onClickItemListener
    }

    fun observerListEmpty(observerListEmpty: ObserverListEmpty) {
        this.observerListEmpty = observerListEmpty
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding, listItems, onClickItemListener)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(
            listItems[position],
            supportFragmentManager,
            position.toString()
        )
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    fun deleteItem(item: Item) {
        notifyItemRemoved(listItems.indexOf(item))
        listItems.remove(item)
        observerListEmpty?.observer(listItems)
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding,
        private val listItems: ArrayList<Item>,
        private val onClickItemListener: OnClickItemListener?
    ) : RecyclerView.ViewHolder(binding.root), DeleteItemInterface {

        init {
            itemView.setOnClickListener {
                val positionRcy = adapterPosition
                if (positionRcy != RecyclerView.NO_POSITION) {
                    listItems[positionRcy].id?.let { id ->
                        onClickItemListener?.setOnClickItemListener(
                            id
                        )
                    }
                }
            }
        }

        fun bind(
            item: Item,
            supportFragmentManager: FragmentManager,
            position: String
        ) = binding.apply {
            item.apply {
                tvNameItem.text = name
                tvQuantityItem.text = itemView
                    .context
                    .getString(
                        R.string.title_quantity_item_with_value,
                        quantity.toString()
                    )
                tvValueItem.text = itemView
                    .context
                    .getString(
                        R.string.text_value_details,
                        TextFormatter.setCurrency(value)
                    )
                ibDeleteItem.setOnClickListener {
                    val deleteItemDialog = DeleteItemDialog()
                    deleteItemDialog.setInstance(
                        item,
                        this@CartViewHolder
                    )
                    deleteItemDialog.show(
                        supportFragmentManager,
                        ""
                    )
                }

                root.contentDescription = itemView
                    .context
                    .getString(
                        R.string.content_description_item_cart,
                        position,
                        name,
                        TextFormatter.setCurrency(value)
                    )
            }
        }

        override fun notifyItemDelete(item: Item) {
            deleteItem(item)
        }
    }
}