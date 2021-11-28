package com.burhanrashid52.photoediting.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * @author [Burhanuddin Rashid](https://github.com/burhanrashid52)
 * @version 0.1.2
 * @since 5/25/2018
 */
abstract class BaseFragment : Fragment() {
    protected abstract val layoutId: Int
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        require(layoutId != 0) { "Invalid layout id" }
        return inflater.inflate(layoutId, container, false)
    }
}