package com.example.tipcalculator.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun InputField(modifier: Modifier = Modifier,
               valueState: MutableState<String>,
               labelId:String,
               enabled:Boolean,
               isSingleLine:Boolean,
               keyboardType: KeyboardType = KeyboardType.Number,
               imeAction: ImeAction = ImeAction.Next,
               onAction:KeyboardActions = KeyboardActions.Default,) {

    OutlinedTextField(
    value = valueState.value,
    onValueChange = { newValue -> valueState.value = newValue },
    modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
    label = { Text(text = labelId) },
        leadingIcon = {Icon(imageVector = Icons.Rounded.AttachMoney, contentDescription = "Money Icon")},
    enabled = enabled,
    singleLine = isSingleLine,
    keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = keyboardType,
        imeAction = imeAction
    ),
    keyboardActions = onAction
)
}