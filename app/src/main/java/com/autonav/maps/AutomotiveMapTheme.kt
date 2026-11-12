package com.autonav.maps

/**
 * Automotive dark mode styling definition for Google Maps Platform.
 *
 * Implements a low-glare dark charcoal cabin aesthetic:
 * - Deep dark background (#0D1014) to prevent night blindness
 * - Minimal POI clutter to keep driver focus on roadways
 * - High-contrast route/highway visibility with muted steel-blue accents
 */
object AutomotiveMapTheme {

    const val MAP_STYLE_AUTOMOTIVE_DARK = """
    [
      {
        "elementType": "geometry",
        "stylers": [
          { "color": "#0D1014" }
        ]
      },
      {
        "elementType": "labels.icon",
        "stylers": [
          { "visibility": "off" }
        ]
      },
      {
        "elementType": "labels.text.fill",
        "stylers": [
          { "color": "#94A3B8" }
        ]
      },
      {
        "elementType": "labels.text.stroke",
        "stylers": [
          { "color": "#0D1014" },
          { "weight": 2 }
        ]
      },
      {
        "featureType": "administrative",
        "elementType": "geometry",
        "stylers": [
          { "color": "#26303D" }
        ]
      },
      {
        "featureType": "poi",
        "stylers": [
          { "visibility": "off" }
        ]
      },
      {
        "featureType": "road",
        "elementType": "geometry.fill",
        "stylers": [
          { "color": "#1B222C" }
        ]
      },
      {
        "featureType": "road",
        "elementType": "geometry.stroke",
        "stylers": [
          { "color": "#141920" }
        ]
      },
      {
        "featureType": "road",
        "elementType": "labels.text.fill",
        "stylers": [
          { "color": "#ECEFF4" }
        ]
      },
      {
        "featureType": "road.arterial",
        "elementType": "geometry.fill",
        "stylers": [
          { "color": "#222D3B" }
        ]
      },
      {
        "featureType": "road.highway",
        "elementType": "geometry.fill",
        "stylers": [
          { "color": "#2D3F54" }
        ]
      },
      {
        "featureType": "road.highway",
        "elementType": "geometry.stroke",
        "stylers": [
          { "color": "#1F2C3B" }
        ]
      },
      {
        "featureType": "transit",
        "stylers": [
          { "visibility": "off" }
        ]
      },
      {
        "featureType": "water",
        "elementType": "geometry",
        "stylers": [
          { "color": "#141920" }
        ]
      },
      {
        "featureType": "water",
        "elementType": "labels.text.fill",
        "stylers": [
          { "color": "#4B8BBE" }
        ]
      }
    ]
    """
}

