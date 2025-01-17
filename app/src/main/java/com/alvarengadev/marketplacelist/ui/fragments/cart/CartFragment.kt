package com.alvarengadev.marketplacelist.ui.fragments.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.alvarengadev.marketplacelist.R
import com.alvarengadev.marketplacelist.databinding.FragmentCartBinding
import com.alvarengadev.marketplacelist.ui.components.bottomsheet.BottomSheetOnboarding
import com.alvarengadev.marketplacelist.ui.components.bottomsheet.`interface`.ButtonGotItClickListener
import com.alvarengadev.marketplacelist.ui.fragments.cart.adapter.CartAdapter
import com.alvarengadev.marketplacelist.ui.fragments.cart.adapter.ObserverListEmpty
import com.alvarengadev.marketplacelist.ui.fragments.cart.adapter.OnClickItemListener
import com.alvarengadev.marketplacelist.ui.fragments.cart.dialog.feature.FeatureClearCartDialog
import com.alvarengadev.marketplacelist.utils.Constants
import com.alvarengadev.marketplacelist.utils.PreferencesManager
import com.alvarengadev.marketplacelist.utils.extensions.createSnack
import com.alvarengadev.marketplacelist.utils.extensions.goneWithoutAnimation
import com.alvarengadev.marketplacelist.utils.extensions.visibilityWithoutAnimation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart), ObserverListEmpty {

    private var preferencesManager: PreferencesManager? = null
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        context?.let { PreferencesManager.initializeInstance(it) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesManager = PreferencesManager.instance
        setupComponents()
        setupEvents()
        listenToRegistrationViewModelEvents()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupComponents() = binding.apply {
        footerCart
            .setTextButton(getString(R.string.footer_button_text))
            .setActionButton {
                findNavController().navigate(R.id.action_cartFragment_to_addFragment)
            }

        ibSettings.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_settings_graph)
        }

        ibMore.setOnClickListener {
            val popupMenu = context?.let { context -> PopupMenu(context, it) }
            popupMenu?.menuInflater?.inflate(R.menu.menu_popup_cart, popupMenu.menu)
            popupMenu?.setOnMenuItemClickListener{
                cartViewModel.deleteAllItemsFromDatabase()
                true
            }
            popupMenu?.show()
        }

        if (preferencesManager?.getViewOnboardingWelcome() == false) {
            val bottomSheet = BottomSheetOnboarding()
            bottomSheet.setButtonGotItClickListener(object : ButtonGotItClickListener {
                override fun onClick() {
                    preferencesManager?.setViewOnboardingWelcome()
                    bottomSheet.dismiss()
                }
            }).show(childFragmentManager, Constants.BOTTOM_SHEET_WELCOME)
        }
    }

    private fun listenToRegistrationViewModelEvents() = binding.apply {
        with(cartViewModel) {
            registrationStateEvent.observe(viewLifecycleOwner) { registrationState ->
                if (registrationState is CartViewModel.CartListState.LoadingList) {
                    pbLoadingList.visibilityWithoutAnimation()
                }

                if (registrationState is CartViewModel.CartListState.ListEmpty) {
                    showList(false)
                    footerCart.setCartValue()
                }

                if (registrationState is CartViewModel.CartListState.SuccessList) {
                    val adapterListCart = CartAdapter()
                    adapterListCart.apply {
                        submitList(registrationState.listItems)
                        setSupportFragmentManager(parentFragmentManager)
                        observerListEmpty(this@CartFragment)
                        setOnClickItemListener(object : OnClickItemListener {
                            override fun setOnClickItemListener(itemId: Int) {
                                val directions = CartFragmentDirections
                                    .actionCartFragmentToDetailsFragment(itemId)

                                findNavController()
                                    .navigate(directions)
                            }
                        })
                    }


                    rcyCartItem.adapter = adapterListCart

                    showList(true)
                    viewDialogAlertFeatureClearCart()
                    footerCart.setCartValue(registrationState.total)
                }

                if (registrationState is CartViewModel.CartListState.Result) {
                    if (registrationState.isSuccessful) {
                        cartViewModel.getAllItemsFromDatabase()
                    } else {
                        root.createSnack(R.string.alert_list_empty)
                    }
                    cartViewModel.resetClearCart()
                }

                if (registrationState is CartViewModel.CartListState.Dialog) {
                    showDialogFeatureClearCart()
                    cartViewModel.resetClearCart()
                }
            }
        }
    }

    private fun setupEvents() = cartViewModel.apply {
        getAllItemsFromDatabase()
    }

    private fun showDialogFeatureClearCart() {
        if (preferencesManager?.getViewFeatureClearCart() == false && preferencesManager?.getViewOnboardingWelcome() == true) {
            val dialog = FeatureClearCartDialog()
            dialog.show(childFragmentManager, Constants.DIALOG_FEATURE_CLEAR_CART)
        }
    }

    private fun showList(isShow: Boolean) = binding.apply {
        pbLoadingList.goneWithoutAnimation()
        if (isShow) {
            rcyCartItem.visibilityWithoutAnimation()
            containerListEmpty.goneWithoutAnimation()
        } else {
            rcyCartItem.goneWithoutAnimation()
            containerListEmpty.visibilityWithoutAnimation()
        }
    }

    override fun observer(total: Double, isEmpty: Boolean) {
        binding.footerCart.apply {
            if (total > 0.0) {
                setCartValue(total)
            }

            if (isEmpty) {
                showList(false)
                setCartValue()
            }
        }
    }
}