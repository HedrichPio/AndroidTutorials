package com.hedrich.shoppinglistapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class ShoppingItem(
    val id:Int,
    var name:String,
    var quantity:Int,
    var isEditing:Boolean=false
)

@Composable
fun ShoppingListApp(modifier: Modifier){
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQty by remember { mutableStateOf("") }
    
    Column(modifier = modifier, verticalArrangement = Arrangement.Center){
        Button(onClick = {showDialog=true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = "Add Item")
        }
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            items(sItems){ item ->
                if(item.isEditing){
                    ShoppingItemEditor(
                        item = item,
                        onEditComplete = {
                            editedName,editedQuantity ->
                            sItems = sItems.map{ it.copy(isEditing = false)}
                            val editedItem = sItems.find { it.id == item.id }
                            editedItem?.let {
                                it.name = editedName
                                it.quantity = editedQuantity
                            }
                    })

                }
                else{
                    ShoppingListItem(
                        item = item,
                        onEditClick = { sItems = sItems.map { it.copy(isEditing = it.id==item.id) } },
                        onDeleteClick = { sItems = sItems - item }
                    )
                }
            }
        }
    }

    if(showDialog){
        AlertDialog(
            onDismissRequest = { showDialog=false },
            confirmButton = {
                Row (modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly){
                    Button(onClick = { showDialog=false }) {
                        Text(text = "Cancel")
                    }
                    Button(onClick = {
                        if (itemName.isNotBlank()){
                            val newItem = ShoppingItem(id = sItems.size + 1, name = itemName, quantity = itemQty.toInt())
                            sItems = sItems + newItem
                            showDialog=false
                            itemName=""
                            itemQty=""
                        }
                    }) {
                        Text(text = "Add")
                    }
                }
            },
            title = {Text(text = "Add Shopping Item")},
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = {itemName=it},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text(text = "Enter Item Name")})
                    OutlinedTextField(
                        value = itemQty,
                        onValueChange = {itemQty=it},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text(text = "Enter Quantity")})
                }
            })
    }
}


@Composable
fun ShoppingListItem(item: ShoppingItem, onEditClick: () -> Unit, onDeleteClick: () -> Unit){

    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .border(border = BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(20))){

        Text(text = item.name, modifier = Modifier.padding(16.dp,0.dp))
        Text(text = "Qty: ${item.quantity}")

        Row (horizontalArrangement = Arrangement.SpaceEvenly,verticalAlignment = Alignment.CenterVertically){
            IconButton(onClick = { onEditClick() }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = { onDeleteClick() }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun ShoppingItemEditor(item:ShoppingItem, onEditComplete:(String,Int)-> Unit){
    var editedName by remember { mutableStateOf(item.name) }
    var editedQty by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember{ mutableStateOf(item.isEditing) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.LightGray)
            .border(border = BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(20)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            TextField(value = editedName, onValueChange = {editedName=it}, singleLine = true, modifier = Modifier
                .wrapContentSize()
                .padding(8.dp))
            TextField(value = editedQty, onValueChange = {editedQty=it}, singleLine = true,modifier = Modifier
                .wrapContentSize()
                .padding(8.dp))
        }
        Button(onClick = {
            isEditing=false
            onEditComplete(editedName,editedQty.toIntOrNull() ?: 1)
        }) {
            Text(text = "Save")
        }
    }
}