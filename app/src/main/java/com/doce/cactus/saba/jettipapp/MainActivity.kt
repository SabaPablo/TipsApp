package com.doce.cactus.saba.jettipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.doce.cactus.saba.jettipapp.components.InputField
import com.doce.cactus.saba.jettipapp.ui.theme.JetTipAppTheme
import com.doce.cactus.saba.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {

                val totalPerPersonState = remember {
                    mutableStateOf(0.0)
                }


                Column() {
                    TopHeader(totalPerPersonState)
                    MainContent(totalPerPersonState)
                }

            }
        }
    }
    @Composable
    fun MyApp(content: @Composable ()-> Unit){
        JetTipAppTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                color = MaterialTheme.colors.background
            ) {
                content()
            }
        }
    }

    @Composable
    fun TopHeader(totalPerPerson: MutableState<Double>){
        Surface(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(corner = CornerSize(12.dp))),
            color = Color(0xFFE9D7f7)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val total = "%.2f".format(totalPerPerson.value)
                Text(text = "Total per Person",
                    style = MaterialTheme.typography.h5
                )
                Text(text = "$${total}", style = MaterialTheme.typography.h4
                , fontWeight = FontWeight.ExtraBold)
            }
        }
    }

    @Composable
    fun MainContent(totalPerPersonState: MutableState<Double>) {

        val splitByState = remember {
            mutableStateOf(1)
        }

        val tipAmountState = remember {
            mutableStateOf(0.0)
        }

        BillForm(
            totalPerPersonState = totalPerPersonState,
            splitByState = splitByState,
            tipAmountState = tipAmountState
        )

    }



    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun BillForm(
        modifier: Modifier= Modifier,
        range:IntRange = 1..100,
        splitByState: MutableState<Int>,
        tipAmountState: MutableState<Double>,
        totalPerPersonState: MutableState<Double>,
        onValChange: (String) -> Unit = {}

    ){
        val totalBillState = remember{
            mutableStateOf("")
        }
        val validState = remember(totalBillState.value) {
            totalBillState.value.trim().isNotEmpty()
        }


        val sliderPositionState = remember {
            mutableStateOf(0f)
        }
        val tipPercentage = (sliderPositionState.value * 100).toInt()





        val keyboardController = LocalSoftwareKeyboardController.current
        Surface(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 2.dp, color = Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(
                    valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValChange(totalBillState.value.trim())
                        totalPerPersonState.value =
                            calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage=tipPercentage)

                        keyboardController?.hide()
                    }
                )
                if(validState){
                    Row (
                        modifier = Modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.Start
                            ){
                        Text(text = "Split",
                        modifier= Modifier.align(alignment = Alignment.CenterVertically))
                        Spacer(modifier = Modifier.width(120.dp))
                        Row(
                            modifier = modifier.padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            RoundIconButton(
                                imageVector = Icons.Default.Remove,
                                onClick = {

                                    if(splitByState.value<=1){
                                        splitByState.value = 1
                                    }else{
                                        splitByState.value = splitByState.value -1
                                        totalPerPersonState.value =
                                            calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage=tipPercentage)
                                    }
                                }
                            )
                            Text(
                                text = splitByState.value.toString(),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(horizontal = 9.dp)
                            )
                            RoundIconButton(
                                imageVector = Icons.Default.Add,
                                onClick = {
                                    splitByState.value +=1
                                    totalPerPersonState.value =
                                        calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage=tipPercentage)

                                }
                            )
                        }
                    }
                // Tip Row
                    Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                        Text(text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically))
                        Spacer(modifier = Modifier.width(200.dp))
                        Text(text = "$ ${tipAmountState.value}")
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${tipPercentage}%")
                        Spacer(modifier = Modifier.height(14.dp))

                    //Slider
                        Slider(
                            value = sliderPositionState.value,
                            onValueChange = {newVal ->
                                sliderPositionState.value = newVal

                                tipAmountState.value =
                                    calculateTotalTip(
                                        totalBillState.value.toDouble(),
                                        tipPercentage)
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage)
                            },
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            steps = 20,
                            onValueChangeFinished = {

                            }
                        )
                    }
                }else{
                    Box(){}
                }
            }
        }
    }

}

