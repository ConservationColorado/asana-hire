export function Modal(props) {
    return <div className="modal">
        <p>Hey, how's it going?</p>
        <button className="btn-primary" onClick={props.onCancel}>Cancel</button>
        <div className="divider"/>
        <button className="btn-primary" onClick={props.onConfirm}>OK</button>
    </div>
}
