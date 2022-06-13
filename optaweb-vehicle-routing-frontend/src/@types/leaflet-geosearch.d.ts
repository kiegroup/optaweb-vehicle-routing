// eslint-disable-next-line max-classes-per-file
declare module 'leaflet-geosearch' {

  class Provider<ProviderOptions> {
    constructor(options?: EndpointUrlParams & ProviderOptions);

    search(searchParam: SearchParam): Promise<SearchResult[]>;
  }

  interface EndpointUrlParams {
    params: object;
  }

  interface SearchParam {
    query: string;
  }

  interface SearchResult {
    /**
     * Longitude.
     */
    x: number;
    /**
     * Latitude.
     */
    y: number;
    label: string;
    bounds: [
      [number, number],
      [number, number]];
    raw: any;
  }

  export class OpenStreetMapProvider extends Provider<{}> {
  }
}
