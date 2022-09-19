/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.lunchtray.data.DataSource
import java.text.NumberFormat

class OrderViewModel : ViewModel() {

    val menuItems = DataSource.menuItems

    private var previousEntreePrice = 0.0
    private var previousSidePrice = 0.0
    private var previousAccompanimentPrice = 0.0

    private val taxRate = 0.08

    private val _entree = MutableLiveData<MenuItem?>()
    val entree: LiveData<MenuItem?> = _entree

    private val _side = MutableLiveData<MenuItem?>()
    val side: LiveData<MenuItem?> = _side

    private val _accompaniment = MutableLiveData<MenuItem?>()
    val accompaniment: LiveData<MenuItem?> = _accompaniment

    private val _subtotal = MutableLiveData(0.0)
    val subtotal: LiveData<String> = Transformations.map(_subtotal) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    private val _total = MutableLiveData(0.0)
    val total: LiveData<String> = Transformations.map(_total) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    private val _tax = MutableLiveData(0.0)
    val tax: LiveData<String> = Transformations.map(_tax) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    fun setEntree(entree: String) {

        if (_entree.value != null) {
            _subtotal.value = _subtotal.value?.minus(previousEntreePrice)
            previousEntreePrice = menuItems[entree]!!.price
        } else {
            previousEntreePrice = menuItems[entree]!!.price
        }

        _entree.value = menuItems[entree]
        updateSubtotal(menuItems[entree]!!.price)
    }

    fun setSide(side: String) {
        if (_side.value != null) {
            _subtotal.value = (_subtotal.value)?.minus(previousSidePrice)
            previousSidePrice = menuItems[side]!!.price
        } else {
            previousSidePrice = menuItems[side]!!.price
        }

        _side.value = menuItems[side]
        updateSubtotal(menuItems[side]!!.price)
    }

    fun setAccompaniment(accompaniment: String) {
        if (_accompaniment.value != null) {
            _subtotal.value = (_subtotal.value)?.minus(previousAccompanimentPrice)
            previousAccompanimentPrice = menuItems[accompaniment]!!.price
        } else {
            previousAccompanimentPrice = menuItems[accompaniment]!!.price
        }

        _accompaniment.value = menuItems[accompaniment]
        updateSubtotal(menuItems[accompaniment]!!.price)
    }


    private fun updateSubtotal(itemPrice: Double) {
        if (_subtotal.value != null) {
            _subtotal.value = (_subtotal.value)?.plus(itemPrice)
        } else {
            _subtotal.value = itemPrice
        }

        calculateTaxAndTotal()
    }

    fun calculateTaxAndTotal() {
        _tax.value = (_subtotal.value)!!.times(taxRate)
        _total.value = (_tax.value)?.plus(_subtotal.value!!)
    }

    fun resetOrder() {
        previousEntreePrice = 0.0
        previousSidePrice = 0.0
        previousAccompanimentPrice = 0.0
        _subtotal.value = 0.0
        _tax.value = 0.0
        _total.value = 0.0
        _entree.value = null
        _side.value = null
        _accompaniment.value = null
    }
}
