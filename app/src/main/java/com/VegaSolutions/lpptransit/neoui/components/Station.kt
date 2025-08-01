package com.VegaSolutions.lpptransit.neoui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.VegaSolutions.lpptransit.neoui.ui.theme.LocalTravanaColorScheme
import com.VegaSolutions.lpptransit.neoui.ui.theme.Typography
import com.VegaSolutions.lpptransit.neoui.ui.theme.montserratFamily
import com.VegaSolutions.lpptransit.neoui.ui.theme.notoSansFamily


data class Station(
    val id: String,
    val name: String,
    val towards: Boolean,
    val routes: List<Route>
)

data class Route(
    val number: String,
    val name: String,
    val arrivals: List<Arrival>
)

data class Arrival(
    val isLive: Boolean,
    val isGarage: Boolean,
    val eta: Int,
)

private val example = Station(
    id = "10000",
    name = "Pošta",
    towards = true,
    routes = listOf(
        Route(
            number = "2",
            name = "ZELENA JAMA",
            arrivals = listOf(
                Arrival(
                    isLive = true,
                    isGarage = false,
                    eta = 0
                ),
                Arrival(
                    isLive = false,
                    isGarage = false,
                    eta = 22
                ),
                Arrival(
                    isLive = false,
                    isGarage = false,
                    eta = 2
                ),
                Arrival(
                    isLive = true,
                    isGarage = false,
                    eta = 10
                ),
            )
        ),
        Route(
            number = "19B",
            name = "TOMAČEVO",
            arrivals = listOf(
                Arrival(
                    isLive = true,
                    isGarage = false,
                    eta = 1
                ),
                Arrival(
                    isLive = false,
                    isGarage = false,
                    eta = 22
                ),
                Arrival(
                    isLive = false,
                    isGarage = false,
                    eta = 2
                ),
                Arrival(
                    isLive = false,
                    isGarage = false,
                    eta = 5
                ),
                Arrival(
                    isLive = true,
                    isGarage = false,
                    eta = 10
                ),
                Arrival(
                    isLive = false,
                    isGarage = false,
                    eta = 55
                ),
                Arrival(
                    isLive = false,
                    isGarage = false,
                    eta = 60
                ),
            )
        )
    )
)

@Preview
@Composable
fun Station(station: Station = example) {
    Column(Modifier.background(Color.White), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = "location",
                tint = LocalTravanaColorScheme.current.alertElement,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = station.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
            Spacer(Modifier.width(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(0.dp)) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowRight,
                    contentDescription = "Arrow",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.size(3.dp))
                Text(
                    text = "Towards",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontFamily = montserratFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }

        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            for (route in station.routes) {
                Route(route)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Route(route: Route) {
    val containsArrival = route.arrivals.any { it.eta == 0 && it.isLive }
    val (live, scheduled) = route.arrivals.sortedBy { it.eta }.partition { it.isLive }
    Surface(
        shape = RoundedCornerShape(8.dp),
        contentColor =
            if (containsArrival)
                LocalTravanaColorScheme.current.specialElementText
            else
                LocalTravanaColorScheme.current.text,
        color =
            if (containsArrival)
                LocalTravanaColorScheme.current.specialElement
            else
                LocalTravanaColorScheme.current.backgroundAlt,
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(24.dp).fillMaxWidth()
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = route.number,
                        fontSize = 20.sp,
                        style = Typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("•")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        route.name,
                        fontFamily = montserratFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.alpha(0.8f).height(IntrinsicSize.Min)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NearMe,
                                contentDescription = "On their way",
                                modifier = Modifier
                                    .fillMaxHeight(0.7f)
                                    .height(1.dp)
                                    .aspectRatio(1f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "On their way",
                                fontFamily = notoSansFamily,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(Modifier.size(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            for (arrival in live) {
                                Arrival(arrival.eta, isLive = arrival.isLive)
                            }
                        }
                    }

                    Column(
                        Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.alpha(0.8f).height(IntrinsicSize.Min)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = "Scheduled",
                                modifier = Modifier
                                    .fillMaxHeight(0.7f)
                                    .height(1.dp)
                                    .aspectRatio(1f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "Scheduled",
                                fontFamily = notoSansFamily,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(Modifier.size(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            for (arrival in scheduled) {
                                Arrival(arrival.eta, isLive = arrival.isLive)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Arrival(time: Int, isLive: Boolean = true) {
        if (time == 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                Text(
                    text = "Arrival",
                    fontWeight = FontWeight.Black,
                    fontFamily = notoSansFamily,
                )
//                Spacer(Modifier.size(4.dp))
//                Icon(
//                    imageVector = Icons.Default.Hail,
//                    contentDescription = "Arrival",
//                    modifier = Modifier.fillMaxHeight().height(1.dp)
//                )
            }
        } else {
            Row {
                Text(
                    text = time.toString(),
                    fontWeight = if (isLive) FontWeight.Bold else FontWeight.Normal,
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = "min",
                    fontSize = 10.sp
                )
            }
        }
}