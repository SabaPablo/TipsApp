package com.doce.cactus.saba.jettipapp

fun calculateTotalTip(totalBill: Double, tipPercentage: Int): Double {
    return if(totalBill > 1 && totalBill.toString().isNotEmpty())
        (totalBill * tipPercentage) / 100 else 0.0

}

fun calculateTotalPerPerson(
    totalBill:Double,
    splitBy:Int,
    tipPercentage: Int
):Double{
    val bill =totalBill + calculateTotalTip(totalBill,tipPercentage)
    return  bill/ splitBy
}