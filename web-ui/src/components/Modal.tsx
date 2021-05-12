import React from 'react';

interface ModalProps {
    className?: string;
    onCloseModal?: Function;
}

export default class Modal extends React.Component<ModalProps, {}> {

    componentDidMount() {
        document.body.classList.add('modal-open');
        document.addEventListener('keydown', this.handleEscape);
    }

    componentWillUnmount() {
        document.body.classList.remove('modal-open');
        document.removeEventListener('keydown', this.handleEscape);
    }

    handleEscape = (e: any): any => {
        if (e.key === 'Escape') {
            if (this.props.onCloseModal) {
                this.props.onCloseModal();
            }
        }
    }

    render() {
        const { className: css } = this.props;
        return <div className='modal-layer'>
            <div className='modal-underlay' />
            <div className='modal d-block' tabIndex={-1} role='dialog'>
                <div className={'modal-dialog ' + (css || '')} role='document'>
                    {this.props.children}
                </div>
            </div>
        </div>
    }
}
