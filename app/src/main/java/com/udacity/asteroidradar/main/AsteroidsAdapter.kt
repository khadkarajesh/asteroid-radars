package com.udacity.asteroidradar.main

import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.base.BaseRecyclerViewAdapter
import com.udacity.asteroidradar.domain.AsteroidDataItem

class AsteroidsAdapter(callBack: (selectedAsteroid: AsteroidDataItem) -> Unit): BaseRecyclerViewAdapter<AsteroidDataItem>(callBack){
    override fun getLayoutRes(viewType: Int) = R.layout.item_asteroid
}