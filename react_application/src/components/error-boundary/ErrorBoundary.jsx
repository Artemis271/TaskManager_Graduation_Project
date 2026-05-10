import React from 'react';
import './error-boundary.css';

export default class ErrorBoundary extends React.Component {
    constructor(props) {
        super(props);
        this.state = { hasError: false, error: null };
    }

    static getDerivedStateFromError(error) {
        return { hasError: true, error };
    }

    componentDidCatch(error, info) {
        console.error('ErrorBoundary caught:', error, info);
    }

    handleReset = () => {
        this.setState({ hasError: false, error: null });
    };

    render() {
        if (this.state.hasError) {
            return (
                <div className="error-boundary">
                    <div className="error-boundary__box">
                        <h2 className="error-boundary__title">Что-то пошло не так</h2>
                        <p className="error-boundary__message">
                            {this.state.error?.message || 'Произошла неожиданная ошибка'}
                        </p>
                        <button className="error-boundary__btn" onClick={this.handleReset}>
                            Попробовать снова
                        </button>
                    </div>
                </div>
            );
        }

        return this.props.children;
    }
}
