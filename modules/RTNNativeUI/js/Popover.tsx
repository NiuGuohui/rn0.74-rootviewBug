import { cloneElement, isValidElement, PropsWithChildren, ReactNode } from 'react';
import { useEffect, useId, useLayoutEffect, useRef } from 'react';
import { AppRegistry, NativeEventEmitter, Text, View } from 'react-native';
import { NativeUI } from './NativeUI';

const popoverRootViewName = '__POPOVER__';
if (!AppRegistry.getAppKeys().includes(popoverRootViewName)) {
  AppRegistry.registerComponent(popoverRootViewName, (): any => () => (
    <View>
      <Text>Hello!</Text>
    </View>
  ));
}

interface PopoverProps {
  width: number;
  height: number;
  visible: boolean;
  onVisibleChange: (visible: boolean) => void;
}

export const Popover = (props: PropsWithChildren<PopoverProps>) => {
  const viewId = useId();
  const visibleChangeFn = useRef<PopoverProps['onVisibleChange']>();
  visibleChangeFn.current = props.onVisibleChange;

  useLayoutEffect(() => {
    NativeUI?.createPopover(viewId, popoverRootViewName, {
      width: props.width,
      height: props.height,
    });
    const sub = new NativeEventEmitter().addListener(
      'popoverVisibleChange',
      (e: { viewId: string; visible: boolean }) => {
        if (e.viewId === viewId) {
          visibleChangeFn.current?.(e.visible);
        }
      }
    );
    return () => {
      sub.remove();
      NativeUI?.changePopoverVisible(viewId, false, true);
    };
  }, [viewId, props.width, props.height]);

  useEffect(() => {
    if (typeof props.visible === 'boolean') {
      NativeUI?.changePopoverVisible(viewId, props.visible, undefined);
    }
  }, [viewId, props.visible]);

  if (isValidElement(props.children)) {
    // @ts-ignore
    return cloneElement(props.children, { nativeID: viewId });
  }
  throw new Error('Popover must be wrapped with a element that can receive nativeID prop!');
};
