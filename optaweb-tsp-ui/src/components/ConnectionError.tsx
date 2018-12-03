import {
  Button,
  Modal,
  Text,
  TextContent,
  TextVariants,
} from '@patternfly/react-core';
import { IconType } from '@patternfly/react-icons/dist/js/createIcon';
import * as React from 'react';

export interface IConnectionErrorProps {
  title: string;
  message: string;
  icon?: IconType;
}

export default class ConnectionError extends React.Component<
  IConnectionErrorProps
> {
  constructor(props: IConnectionErrorProps) {
    super(props);
  }

  render() {
    const { title, message, icon } = this.props;
    return (
      <Modal
        title="Modal Header"
        isOpen={true}
        actions={[
          <Button
            key="confirm"
            variant="primary"
            onClick={() => console.log('batta')}
          >
            Confirm
          </Button>,
        ]}
      >
        <TextContent>
          <Text component={TextVariants.h1}>
            {icon ? icon : undefined}
            {title}
            <Text component={TextVariants.small}>{message}</Text>
          </Text>
        </TextContent>
      </Modal>
    );
  }
}
