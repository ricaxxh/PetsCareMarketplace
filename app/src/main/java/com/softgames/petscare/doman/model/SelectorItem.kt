package com.softgames.petscare.doman.model

data class SelectorItem(val title: String, val icon: Int){
    override fun toString(): String {
        return title
    }
}
