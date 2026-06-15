package com.example.parkmate.data.seed

import com.example.parkmate.data.model.Attraction
import com.example.parkmate.data.model.Park

object ParkSeedData {
    val parks: List<Park> = listOf(
        Park(
            id = "yosemite",
            name = "Yosemite National Park",
            location = "California",
            description = "Granite cliffs, waterfalls, giant sequoias, and classic Sierra Nevada viewpoints.",
            imageUrl = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee",
            categories = listOf("Hiking", "Photos", "Waterfalls"),
            attractions = listOf(
                Attraction(
                    id = "tunnel-view",
                    parkId = "yosemite",
                    name = "Tunnel View",
                    description = "A famous overlook with views of El Capitan, Half Dome, and Bridalveil Fall.",
                    trailInfo = "Short walk from the parking area. Best used as a quick scenic stop.",
                    photoTips = "Arrive near sunrise or golden hour for soft light across the valley.",
                    nearbyFood = "Yosemite Valley has casual dining and picnic areas within driving distance.",
                    safetyTips = "Expect crowded parking and stay behind overlook barriers.",
                    imageUrl = "https://images.unsplash.com/photo-1472396961693-142e6e269027"
                ),
                Attraction(
                    id = "mist-trail",
                    parkId = "yosemite",
                    name = "Mist Trail",
                    description = "A signature hike passing Vernal Fall and Nevada Fall.",
                    trailInfo = "Moderate to strenuous hike with steep stone steps and wet sections.",
                    photoTips = "Use a waterproof case near Vernal Fall because spray can be heavy.",
                    nearbyFood = "Pack snacks before entering the trail; food options are back in Yosemite Valley.",
                    safetyTips = "Wear grippy shoes and avoid climbing over rails near waterfalls.",
                    imageUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b"
                )
            )
        ),
        Park(
            id = "yellowstone",
            name = "Yellowstone National Park",
            location = "Wyoming, Montana, Idaho",
            description = "A geothermal landscape with geysers, hot springs, wildlife, and wide open valleys.",
            imageUrl = "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429",
            categories = listOf("Geyser", "Wildlife", "Scenic"),
            attractions = listOf(
                Attraction(
                    id = "old-faithful",
                    parkId = "yellowstone",
                    name = "Old Faithful",
                    description = "Yellowstone's most famous geyser with predictable eruption windows.",
                    trailInfo = "Boardwalk loop around the geyser basin with mostly flat walking.",
                    photoTips = "Frame the geyser from the boardwalk with enough sky for the eruption plume.",
                    nearbyFood = "Old Faithful area has lodges, cafes, and general store options.",
                    safetyTips = "Stay on boardwalks; thermal ground can be thin and dangerous.",
                    imageUrl = "https://images.unsplash.com/photo-1564459123635-16ba8b410326"
                ),
                Attraction(
                    id = "grand-prismatic",
                    parkId = "yellowstone",
                    name = "Grand Prismatic Spring",
                    description = "A bright thermal spring known for blue, orange, and yellow rings.",
                    trailInfo = "Boardwalk access plus an overlook trail from the Fairy Falls trailhead.",
                    photoTips = "The overlook gives the clearest view of the spring's full color pattern.",
                    nearbyFood = "Food is available near Old Faithful and Madison after driving.",
                    safetyTips = "Do not step off trail or touch thermal water.",
                    imageUrl = "https://images.unsplash.com/photo-1605540436563-5bca919ae766"
                )
            )
        ),
        Park(
            id = "grand-canyon",
            name = "Grand Canyon National Park",
            location = "Arizona",
            description = "A vast canyon landscape with dramatic overlooks, rim trails, and desert light.",
            imageUrl = "https://images.unsplash.com/photo-1474044159687-1ee9f3a51722",
            categories = listOf("Family", "Photos", "Scenic"),
            attractions = listOf(
                Attraction(
                    id = "mather-point",
                    parkId = "grand-canyon",
                    name = "Mather Point",
                    description = "A popular South Rim viewpoint close to the visitor center.",
                    trailInfo = "Easy paved access from the visitor center and nearby shuttle stops.",
                    photoTips = "Sunrise gives strong depth and color across the canyon layers.",
                    nearbyFood = "The visitor center area has snacks; more dining is available in Grand Canyon Village.",
                    safetyTips = "Stay away from exposed edges and watch children closely.",
                    imageUrl = "https://images.unsplash.com/photo-1527333656061-ca7adf608ae1"
                ),
                Attraction(
                    id = "bright-angel-trail",
                    parkId = "grand-canyon",
                    name = "Bright Angel Trail",
                    description = "A classic corridor trail descending below the South Rim.",
                    trailInfo = "Strenuous hiking with steep return climbs; choose turnaround points conservatively.",
                    photoTips = "Early morning light works well on switchbacks and canyon walls.",
                    nearbyFood = "Bring water and food; services are limited once below the rim.",
                    safetyTips = "Do not attempt a rim-to-river day hike without serious preparation.",
                    imageUrl = "https://images.unsplash.com/photo-1615551043360-33de8b5f410c"
                )
            )
        )
    )
}
