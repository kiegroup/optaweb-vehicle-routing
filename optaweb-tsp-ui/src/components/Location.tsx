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

import * as React from "react";

export interface ILocationProps {
  id: number;
  removeDisabled: boolean;
  removeHandler: (id: number) => void;
  selectHandler: (e: any) => void; // FIXME: Event Type
}

const Location: React.SFC<ILocationProps> = ({
  id,
  removeDisabled,
  removeHandler,
  selectHandler
}: ILocationProps) => {
  return (
    <div
      key={id}
      className="ma2 flex bg-animate hover-bg-light-gray"
      onMouseEnter={() => selectHandler(id)}
      onMouseLeave={() => selectHandler(NaN)}
    >
      <span className="w-80 pa2">{`Location ${id}`}</span>
      <button
        type="button"
        disabled={removeDisabled}
        className="w-20 pa2"
        onClick={() => removeHandler(id)}
      >
        x
      </button>
    </div>
  );
};

export default Location;
