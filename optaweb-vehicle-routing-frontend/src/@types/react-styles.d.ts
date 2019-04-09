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

declare module '@patternfly/react-styles' {
  export interface StyleDeclarationStatic {
    __className: string;

    __inject(): void;
  }

  export function isValidStyleDeclaration(
    obj: any,
  ): obj is StyleDeclarationStatic;

  export function createStyleDeclaration(
    className: string,
    rawCss: string,
  ): StyleDeclarationStatic;

  export function isModifier(className: string): boolean;

  export function getModifier(
    styleObject: any,
    modifier: string,
    defaultModifer?: StyleDeclarationStatic | string,
  ): StyleDeclarationStatic | string;

  export function formatClassName(className: string): string;

  export function getCSSClasses(cssString: string): string[];

  export function getInsertedStyles(): string[];

  export function getClassName(obj: StyleDeclarationStatic | string): string;

  export interface StyleSheetStatic {
    parse(cssString: string): StyleSheetValueStatic;

    create<T extends Record<keyof T, any>>(
      styles: T,
    ): Record<keyof T, string>;
  }

  export type StyleSheetValueStatic = {
    modifiers: { [key: string]: StyleDeclarationStatic };
    inject(): void;
  } & {
    [key: string]: any;
  };

  export const StyleSheet: StyleSheetStatic;

  export const css: string;

}
