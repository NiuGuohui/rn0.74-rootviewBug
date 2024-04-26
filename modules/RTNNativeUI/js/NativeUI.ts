import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export { Popover } from './Popover';

export interface Spec extends TurboModule {
  createPopover: (anchorViewId: string, moduleName: string, options: { width: number; height: number }) => void;
  changePopoverVisible: (anchorViewId: string, visible: boolean, destory?: boolean) => void;
}

export const NativeUI = TurboModuleRegistry.get<Spec>('NativeUI') as Spec | null;
