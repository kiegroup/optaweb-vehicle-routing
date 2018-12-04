import { Modal, Text, TextContent, TextVariants } from '@patternfly/react-core';
import * as React from 'react';
import { ReactNode } from 'react';
import './ConnectionError.css';
export interface IConnectionErrorProps {
  title: string;
  message: string;
  icon?: ReactNode;
  help?: string;
}

export default class ConnectionError extends React.Component<
  IConnectionErrorProps
> {
  constructor(props: IConnectionErrorProps) {
    super(props);
  }

  renderHelpBlock() {
    const { help } = this.props;
    return help ? (
      <Text component={TextVariants.small}>{this.props.help}</Text>
    ) : (
      ''
    );
  }

  render() {
    const { title, message, icon } = this.props;
    return (
      <Modal title={title} isOpen={true} style={{ zIndex: 1000000 }}>
        <TextContent>
          <Text component={TextVariants.h3}>
            {icon}
            {message}
            {this.renderHelpBlock()}
          </Text>
        </TextContent>
      </Modal>
    );
  }
}
