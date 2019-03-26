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

package org.optaweb.vehiclerouting.service.demo;

public enum Belgium {
    AALST(50.933333, 4.033333),
    ANDERLECHT(50.833333, 4.333333),
    ANTWERP(51.217778, 4.400278),
    BERINGEN(51.033333, 5.216667),
    BRUGES(51.216667, 3.233333),
    BRUSSELS(50.85, 4.35),
    CHARLEROI(50.4, 4.433333),
    CHATELET(50.4, 4.516667),
    DENDERMONDE(51.033333, 4.1),
    GEEL(51.166667, 5),
    GENK(50.966667, 5.5),
    GHENT(51.05, 3.733333),
    HALLE(50.733333, 4.233333),
    HASSELT(50.93, 5.3375),
    IXELLES(50.833333, 4.366667),
    KORTRIJK(50.833333, 3.266667),
    LA_LOUVIERE(50.466667, 4.183333),
    LEUVEN(50.883333, 4.7),
    LIEGE(50.633333, 5.566667),
    LIER(51.133333, 4.566667),
    LOKEREN(51.1, 3.983333),
    MECHELEN(51.016667, 4.466667),
    MOLENBEEK(50.857778, 4.315833),
    MONS(50.45, 3.95),
    NAMUR(50.466667, 4.866667),
    NINOVE(50.833333, 4.016667),
    ROESELARE(50.933333, 3.116667),
    SCHAERBEEK(50.866667, 4.383333),
    SERAING(50.583333, 5.5),
    SINT_NIKLAAS(51.166667, 4.133333),
    SINT_TRUIDEN(50.8, 5.183333),
    TIENEN(50.8, 4.933333),
    TOURNAI(50.6, 3.383333),
    TURNHOUT(51.316667, 4.95),
    UCCLE(50.8, 4.333333),
    VERVIERS(50.583333, 5.85),
    VILVOORDE(50.933333, 4.416667),
    WAREGEM(50.883333, 3.416667),
    WAVRE(50.716667, 4.6),
    YPRES(50.85, 2.883333);

    Belgium(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public final double lat;
    public final double lng;

    @Override
    public String toString() {
        return name() +
                "[lat=" + lat +
                ", lng=" + lng +
                ']';
    }
}
