import React from "react";
import Footer from "./footer";
import Header from "./header";
import {Container} from "./styles";
import PropTypes from "prop-types";

const DefaultLayout = props => {
  return (
    <>
      <Header title={props.title} btn={props.private} back={props.back} />
        <Container><div className="container">{props.children}</div></Container>
      <Footer />
    </>
  );
};
DefaultLayout.propTypes = {
    title : PropTypes.string.isRequired,
    private: PropTypes.bool,
    back: PropTypes.bool,
    children: PropTypes.any
};
export default DefaultLayout;
