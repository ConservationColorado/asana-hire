import React, {useState} from "react";
import {Modal} from "./Modal";
import {Backdrop} from "./Backdrop";

function Job(props) {
    const [modalIsOpen, setModalIsOpen] = useState(false)

    function buttonHandler() {
        setModalIsOpen(true)
    }

    function closeModalHandler() {
        setModalIsOpen(false)
    }

    return (
        <div>
            <p className="text-gray-500 text-lg">
                My jobs will go here eventually.</p>
            <button className="bg-blue-500 text-white font-bold py-2 px-5" onClick={buttonHandler}>{props.text}</button>

            {modalIsOpen && <Modal onConfirm={closeModalHandler} onCancel={closeModalHandler}/>}
            {modalIsOpen && <Backdrop onCancel={closeModalHandler}/>}
        </div>
    )
}

export default Job;
