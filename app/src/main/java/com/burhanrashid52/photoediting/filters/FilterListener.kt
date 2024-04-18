package com.mhdxbilal007.photoediting.filters

import ja.mhdxbilal007.photoeditor.PhotoFilter

interface FilterListener {
    fun onFilterSelected(photoFilter: PhotoFilter)
}