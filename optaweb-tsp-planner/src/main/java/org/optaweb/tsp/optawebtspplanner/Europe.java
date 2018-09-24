/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaweb.tsp.optawebtspplanner;

public enum Europe {
    AMSTERDAM(52.366667, 4.9),
    ANKARA(39.933333, 32.866667),
    ATHENS(37.983972, 23.727806),
    BELGRADE(44.816667, 20.466667),
    BERLIN(52.516667, 13.388889),
    BERN(46.95, 7.45),
    BRATISLAVA(48.143889, 17.109722),
    BRUSSELS(50.85, 4.35),
    BUCHAREST(44.4325, 26.103889),
    BUDAPEST(47.4925, 19.051389),
    CHISINAU(47, 28.916667),
    COPENHAGEN(55.676111, 12.568333),
    DUBLIN(53.349722, -6.260278),
    HELSINKI(60.170833, 24.9375),
    KIEV(50.45, 30.523333),
    LISBON(38.713889, -9.139444),
    LJUBLJANA(46.055556, 14.508333),
    LONDON(51.507222, -0.1275),
    LUXEMBOURG(49.6106, 6.1328),
    MADRID(40.383333, -3.716667),
    MINSK(53.9, 27.566667),
    MOSCOW(55.75, 37.616667),
    NICOSIA(35.166667, 33.366667),
    OSLO(59.916667, 10.733333),
    PARIS(48.8567, 2.3508),
    PODGORICA(42.441286, 19.262892),
    PRAGUE(50.083333, 14.416667),
    REYKJAVIK(64.133333, -21.933333),
    RIGA(56.948889, 24.106389),
    ROME(41.9, 12.5),
    SARAJEVO(43.866667, 18.416667),
    SKOPJE(42, 21.433333),
    SOFIA(42.7, 23.33),
    STOCKHOLM(59.329444, 18.068611),
    TALLINN(59.437222, 24.745278),
    TIRANA(41.328889, 19.817778),
    VIENNA(48.2, 16.366667),
    VILNIUS(54.683333, 25.283333),
    WARSAW(52.233333, 21.016667),
    ZAGREB(45.816667, 15.983333);

    Europe(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public final double lat;
    public final double lng;
}
