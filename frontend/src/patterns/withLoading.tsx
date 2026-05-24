// PATTERN: Decorator
// HOC care adauga comportament de loading oricarui component fara sa-l modifice.
import React from 'react';

interface WithLoadingProps {
  isLoading: boolean;
}

function withLoading<P extends object>(
  Component: React.ComponentType<P>,
): React.ComponentType<P & WithLoadingProps> {
  const displayName = Component.displayName ?? Component.name ?? 'Component';

  function WrappedComponent({ isLoading, ...props }: P & WithLoadingProps) {
    if (isLoading) {
      return (
        <div className="flex justify-center items-center py-16">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600" />
        </div>
      );
    }
    return <Component {...(props as P)} />;
  }

  WrappedComponent.displayName = `withLoading(${displayName})`;
  return WrappedComponent;
}

export default withLoading;
