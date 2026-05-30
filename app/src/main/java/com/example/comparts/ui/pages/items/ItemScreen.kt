package com.example.comparts.ui.pages.items

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comparts.viewmodel.ItemViewModel

@Composable
fun ItemScreen(
    viewModel: ItemViewModel = viewModel()
) {

    val items = viewModel.items.value

    LazyColumn {

        items(items) { item ->

            Text(
                "${item.item_name} (${item.item_quantity})"
            )
        }
    }
}