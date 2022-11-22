import { Location } from 'history';
import { RouteComponentProps } from 'react-router';
import { shallow, toJson } from 'ui/shallow-test-util';
import { Navigation } from './Navigation';

describe('Navigation', () => {
  it('should activate a navigation link matching the current path', () => {
    const props: RouteComponentProps = {
      location: {
        pathname: '/visits',
      } as Location<unknown>,
    } as RouteComponentProps;

    // const visitsId = '/visits';

    const navigation = shallow(<Navigation {...props} />);
    expect(toJson(navigation)).toMatchSnapshot();

    // FIXME

    // NavItem matching the path should be active
    // const navItems = navigation.find(NavItem).filterWhere((navItem) => navItem.props().itemId === visitsId);
    // expect(navItems).toHaveLength(1);
    // expect(navItems.at(0).props().isActive).toEqual(true);

    // Other NavItems should be inactive
    // navigation.find(NavItem).filterWhere((navItem) => navItem.props().itemId !== visitsId).forEach(
    //   (navItem) => expect(navItem.props().isActive).toEqual(false),
    // );
  });
});
