package com.pineapple.app.model

data class ListingItem(
    var after: String,
    var dist: Int,
    var modhash: String,
    var children: List<PostItem>
)