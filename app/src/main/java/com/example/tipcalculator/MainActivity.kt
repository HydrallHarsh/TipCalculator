package com.example.tipcalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tipcalculator.components.InputField
import com.example.tipcalculator.data.CounterViewModel
import com.example.tipcalculator.ui.theme.TipCalculatorTheme
import com.example.tipcalculator.utils.calculateTotalPerPerson
import com.example.tipcalculator.utils.calculateTotalTip
import com.example.tipcalculator.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipCalculatorApp{
                Scaffold { paddingValues ->
                    Column(modifier = Modifier.padding(paddingValues)) {
                        MainContent()
                    }
                }
            }
        }
    }
}


@Composable
fun TipCalculatorApp(content: @Composable () -> Unit){
    TipCalculatorTheme {
        Surface() {
            content()
            
        }
    }
}


@Preview
@Composable
fun TopHeader(totalPerPers: Double = 0.0) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .padding(12.dp)
        .clip(shape = CircleShape.copy(all = CornerSize(12.dp))), color = Color(0xFFe9d7f7)) {
        Column(modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(text = "Total Per Person", style = MaterialTheme.typography.headlineMedium)
            val total = "%.2f".format(totalPerPers)
            Text(text = "\$$total", style = MaterialTheme.typography.headlineLarge)


        }

    }

}

@Preview
@Composable
fun MainContent() {
    val splitBy = remember {
        mutableStateOf(1)
    }

//    val sliderPosition: MutableState<Float> = remember {
//        mutableStateOf(0f)
//    }

    val totalTipAmt = remember {
        mutableStateOf(0.0)
    }
//    val totalTipAmt: MutableState<Double> = remember {
//        mutableStateOf(0.0)
//    }
    val totalPerPerson = remember {
        mutableStateOf(0.0)
    }
    BillForm(splitByState = splitBy,
        tipAmountState = totalTipAmt,
        totalPerPersonState = totalPerPerson

    ) {

    }
}


@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = {},
) {

    var sliderPosition by remember {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderPosition * 100).toInt()
    val totalBill = rememberSaveable { mutableStateOf("") } //or just remember {}
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(totalBill.value) {
        totalBill.value.trim().isNotEmpty()
    }

    TopHeader(totalPerPers = totalPerPersonState.value)

    Surface(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = CircleShape.copy(all = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)) {

        Column(modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {

            InputField(
                valueState = totalBill, labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if (!valid){
                        return@KeyboardActions
                    }
                    keyboardController?.hide()
                }
            )

            if (valid) {
                Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start) {
                    Text(text = "Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(120.dp))

                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End) {

                        RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                            splitByState.value =
                                if (splitByState.value > 1) splitByState.value - 1 else 1
                            totalPerPersonState.value =
                                calculateTotalPerPerson(totalBill = totalBill.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercent = tipPercentage)
                        })

                        Text(text = "${splitByState.value}",
                            Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp))
                        RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                            if (splitByState.value < range.last) {
                                splitByState.value = splitByState.value + 1

                                totalPerPersonState.value =
                                    calculateTotalPerPerson(totalBill = totalBill.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercent = tipPercentage)
                            }
                        })

                    }
                }
                //Tip Row
                Row(modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.End) {
                    Text(text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically))

                    Spacer(modifier = Modifier.width(200.dp))

                    Text(text = "$${tipAmountState.value}",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically))

                }
                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(text = "$tipPercentage %")
                    Spacer(modifier = Modifier.height(14.dp))
                    Slider(value = sliderPosition,
                        onValueChange = { newVal ->
                            sliderPosition = newVal
                            tipAmountState.value =
                                calculateTotalTip(totalBill = totalBill.value.toDouble(),
                                    tipPercent = tipPercentage)

                            totalPerPersonState.value =
                                calculateTotalPerPerson(totalBill = totalBill.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercent = tipPercentage)
                            Log.d("Slider",
                                "Total Bill-->: ${"%.2f".format(totalPerPersonState.value)}")

                        },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        steps = 5,
                        onValueChangeFinished = {
                            Log.d("Finished", "BillForm: $tipPercentage")
                            //This is were the calculations should happen!
                        })

                }

            }


        }
    }//end isValid

}


@Composable
fun ScreenDemo(model: CounterViewModel) {

    //source: https://www.rockandnull.com/jetpack-compose-viewmodel/
    Column(modifier = Modifier.padding(14.dp)) {
        Demo("This is ${model.getCounter()}", counterViewModel = model) {
            if (it) {
                model.increaseCounter()
            } else {
                model.decreaseCounter()
            }
        }

        if (model.getCounter() > 12) {

            Text(text = "I love this so much!!")


        }
    }



}

@Composable
fun Demo(
    text: String,
    counterViewModel: CounterViewModel,
    onclick: (Boolean) -> Unit = {},
) {
    val isVal = remember {
        mutableStateOf(false)
    }
    Column(verticalArrangement = Arrangement.Center) {
        var isRange by remember {
            mutableStateOf(false)
        }
        isRange = counterViewModel.getCounter() == 12
        Text(text = text, color = if (isRange) Color.Red else Color.LightGray)


        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = {
                    isVal.value = true
                    onclick(isVal.value)
                },
            ) {

                Text(text = "Add 1")
            }

            Button(
                onClick = {
                    isVal.value = false
                    onclick(isVal.value)
                },
            ) {

                BasicText(text = "Minus 1")
            }

        }

    }


}


@Composable
private fun TipSlider(
    modifier: Modifier = Modifier,
    sliderState: MutableState<Float>,
    totalTipState: MutableState<Double>,
    totalBillState: MutableState<String>,
) {
    val tipPercentage = (sliderState.value.toInt())

    val percentage = buildAnnotatedString {
        withStyle(style = SpanStyle(fontSize = 32.sp)) { append(tipPercentage.toString()) }
        append(" %")
    }
    //Slider
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(text = percentage.toString())
        Spacer(modifier = Modifier.height(14.dp))
        Slider(value = sliderState.value,
            onValueChange = {
                sliderState.value = it
                totalTipState.value = calculateTotalTip(totalBill = totalBillState.value.toDouble(),
                    tipPercent = tipPercentage)
                // Log.d("AMT", "TipSlider: $tipPercentage")

//                totalTipState.value = calculateTotalTip(tota
//                    tipPercent = tipPercentage).roundToInt().toString()
            },
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            steps = 100,
            valueRange = (0f..100f))

    }

}


