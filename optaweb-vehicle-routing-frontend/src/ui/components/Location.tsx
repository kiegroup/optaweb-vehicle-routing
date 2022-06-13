import { Button, DataListCell, DataListItem, DataListItemRow, Tooltip } from '@patternfly/react-core';
import { TimesIcon } from '@patternfly/react-icons';
import * as React from 'react';

export interface LocationProps {
  id: number;
  description: string | null;
  removeDisabled: boolean;
  removeHandler: (id: number) => void;
  selectHandler: (id: number) => void;
}

const Location: React.FC<LocationProps> = ({
  id,
  description,
  removeDisabled,
  removeHandler,
  selectHandler,
}) => {
  const [clicked, setClicked] = React.useState(false);

  function shorten(text: string) {
    const first = text.replace(/,.*/, '').trim();
    const short = first.substring(0, Math.min(20, first.length)).trim();
    if (short.length < first.length) {
      return `${short}...`;
    }
    return short;
  }

  return (
    <DataListItem
      isExpanded={false}
      aria-labelledby={`location-${id}`}
      onMouseEnter={() => selectHandler(id)}
      onMouseLeave={() => selectHandler(NaN)}
    >
      <DataListItemRow>
        <DataListCell isFilled>
          {(description && (
            <Tooltip content={description}>
              <span id={`location-${id}`}>{shorten(description)}</span>
            </Tooltip>
          ))
          || <span id={`location-${id}`}>{`Location ${id}`}</span>}
        </DataListCell>
        <DataListCell isFilled={false}>
          <Button
            type="button"
            variant="link"
            isDisabled={removeDisabled || clicked}
            onClick={() => {
              setClicked(true);
              removeHandler(id);
            }}
          >
            <TimesIcon />
          </Button>
        </DataListCell>
      </DataListItemRow>
    </DataListItem>
  );
};

export default Location;
