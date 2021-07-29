import React from "react";
import * as service from "../../../utils/services";
import {NavLink, useHistory} from "react-router-dom";
import {HeaderBox,HeaderContainer} from "./styles";
import PropTypes from "prop-types";

const handleLogOut = e =>{
    e.preventDefault();
    service.logOut();
};
const Header = props => {
    return (
        <HeaderBox>
            <HeaderContainer>
            <h1>{props.title}</h1>
            {props.btn
                &&
                <div className="btns">
                <button className="btn logout" onClick={handleLogOut}>Logout</button>
                <NavLink exact to='/add' className="btn add">Add User</NavLink>
                </div>
                }
            {props.back
            &&
            <div className="btns">
                <button className="btn back" onClick={() =>  window.location.href='/'}>Back</button>
            </div>
            }
            </HeaderContainer>
        </HeaderBox>
    );
}
Header.propTypes = {
    title : PropTypes.string.isRequired,
    btn: PropTypes.bool,
    back: PropTypes.bool
};
export default Header;