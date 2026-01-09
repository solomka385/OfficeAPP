package com.example.buildings.ui.strings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.buildings.R

@Composable
fun buildingsTitle() = stringResource(R.string.buildings_title)

@Composable
fun addBuilding() = stringResource(R.string.add_building)

@Composable
fun noBuildings() = stringResource(R.string.no_buildings)

@Composable
fun tapToAdd() = stringResource(R.string.tap_to_add)

@Composable
fun delete() = stringResource(R.string.delete)

@Composable
fun edit() = stringResource(R.string.edit)

@Composable
fun save() = stringResource(R.string.save)

@Composable
fun back() = stringResource(R.string.back)


@Composable
fun parkingSpaces(count: Int) = stringResource(R.string.parking_spaces, count)

@Composable
fun monthlyRent(amount: String) = stringResource(R.string.monthly_rent, amount)

@Composable
fun typeWithoutParking() = stringResource(R.string.type_without_parking)

@Composable
fun typeWithParking() = stringResource(R.string.type_with_parking)


@Composable
fun buildingDetails() = stringResource(R.string.building_details)